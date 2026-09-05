# Tornar o envio do e-mail de verificação assíncrono

## Contexto

Hoje `EmailService.sendMail` (`src/main/java/com/barbup/barbup_api/infra/services/EmailService.java:18-33`)
chama a API do Resend de forma **síncrona**, bloqueando a thread da
requisição até a resposta HTTP do Resend voltar. Ele é chamado a partir de
`AuthService.sendVerificationEmail` (`src/main/java/com/barbup/barbup_api/services/AuthService.java:72-80`),
que por sua vez é chamado dentro de `AuthService.register` (linha 46) —
ou seja, `POST /auth/register` só responde depois que o e-mail termina de
ser enviado (ou falha).

Isso já foi endereçado de forma "defensiva" no plano anterior
(`docs`/plano de tornar a falha não-bloqueante com try/catch), mas deixar o
envio sempre síncrono ainda significa que a latência do Resend é somada à
latência do endpoint de registro. Este documento analisa como tornar o
envio **assíncrono**, para que `register()` não espere a chamada de rede.

## Restrição importante do Spring: onde colocar `@Async`

`@Async` funciona via proxy (AOP). Duas regras que definem onde ele PODE
ser colocado neste código:

1. O método anotado precisa ser `public` (proxies dinâmicos/CGLIB não
   interceptam métodos `private`/`protected`).
2. A chamada precisa vir de **fora** da classe que declara o método —
   autoinvocação (`this.metodo()` dentro da própria classe) **ignora
   silenciosamente** o `@Async`.

Consequência direta: `AuthService.sendVerificationEmail` é `private` e é
chamado com `this.` dentro da própria `AuthService` — não dá para anotar
esse método com `@Async` e esperar que funcione. O lugar correto é
`EmailService.sendMail`, que já é `public` e já é chamado a partir de
outro bean (`AuthService` → `EmailService`), satisfazendo as duas regras.

## Mudanças propostas

### 1. Habilitar suporte a `@Async` — novo arquivo `infra/config/AsyncConfig.java`

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("email-async-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("Async failure in {}", method.getName(), ex);
    }
}
```

- Pool pequeno é suficiente: envio de e-mail é I/O-bound e pouco frequente
  (hoje só no registro; futuramente também no endpoint de reenvio).
- `getAsyncUncaughtExceptionHandler` só é acionado para métodos `@Async`
  que retornam `void` — ver decisão de tipo de retorno abaixo.

### 2. `infra/services/EmailService.java` — anotar `sendMail` com `@Async`

Duas opções de assinatura:

**Opção A — `void` (fire-and-forget puro)**
- Mais simples; erro tratado só pelo `AsyncUncaughtExceptionHandler`
  global configurado acima.
- Perde-se o retorno (`response.getId()`), que hoje existe mas não é
  usado por ninguém.

**Opção B — `CompletableFuture<String>` (recomendado)**
```java
@Async
public CompletableFuture<String> sendMail(String to, String subject, String htmlBody) {
    CreateEmailOptions params = ...;
    try {
        CreateEmailResponse response = resend.emails().send(params);
        return CompletableFuture.completedFuture(response.getId());
    } catch (ResendException e) {
        return CompletableFuture.failedFuture(e);
    }
}
```
- Mantém o ID do e-mail disponível para quem quiser inspecionar
  (ex.: um futuro endpoint de reenvio pode logar/checar o último envio).
- Permite que o chamador decida explicitamente o que fazer com a falha
  (ver item 3), em vez de depender só do handler global — mais visível e
  mais fácil de testar.
- Cuidado: se ninguém chamar `.exceptionally()`/`.whenComplete()`/`.get()`
  na future retornada, uma falha fica **silenciosa** (ninguém a observa).
  Por isso o item 3 é obrigatório, não opcional, com essa opção.

Recomendação: **Opção B**, por dar visibilidade explícita da falha no
ponto de chamada (`AuthService`) sem depender só de configuração global.

### 3. `services/AuthService.java` — tratar a `CompletableFuture` retornada

```java
private void sendVerificationEmail(User user) {
    String htmlBody = emailTemplareRenderer.render(...); // continua síncrono

    this.emailService.sendMail(user.getEmail(), "Confirme seu cadastro - Barbup", htmlBody)
            .exceptionally(ex -> {
                log.error("Falha ao enviar e-mail de verificação para {}", user.getEmail(), ex);
                return null;
            });
}
```

- A renderização do template (Thymeleaf) **permanece síncrona** — é
  processamento local e rápido; não há motivo para tirá-la da thread da
  requisição, e um erro nela (ex.: template inexistente) é um bug de
  configuração que faz sentido falhar cedo/visivelmente, diferente de uma
  falha de rede do Resend.
- Como `sendMail` agora retorna imediatamente (a chamada real acontece em
  outra thread), o `try/catch` do plano anterior em `register()` deixa de
  ser necessário para a parte de rede — `sendVerificationEmail` não lança
  mais de forma síncrona por causa do Resend. Ainda vale manter/reintroduzir
  um catch ao redor de `emailTemplareRenderer.render(...)` se quiser blindar
  também contra erro de template, mas isso é uma decisão independente da
  parte assíncrona.
- Precisa de `@Slf4j` em `AuthService` (ou em `EmailService`, se logar lá
  dentro do `.exceptionally`) — assim como no plano anterior, este seria o
  primeiro uso de logger no projeto.

## Trade-offs e alternativas consideradas

- **`@Async` do Spring (proposta acima) vs. fila real (RabbitMQ/SQS/etc.)**:
  `@Async` roda em memória, na mesma JVM — se o processo cair entre o
  `register()` retornar e a task assíncrona executar, o e-mail é perdido
  silenciosamente, sem retry. Dado que você já planeja um endpoint de
  reenvio de código, isso é uma limitação aceitável para o estágio atual
  do projeto (não há infraestrutura de fila hoje). Vale documentar essa
  limitação, não construir uma fila agora.
- **`ApplicationEventPublisher` + `@TransactionalEventListener(phase = AFTER_COMMIT)`**:
  seria mais idiomático se a criação do usuário estivesse dentro de uma
  transação (`@Transactional`) e você quisesse garantir que o e-mail só é
  disparado depois do commit no banco. Hoje **não existe `@Transactional`
  em nenhum service do projeto** — introduzir isso só para o e-mail seria
  adicionar uma camada de complexidade nova ao projeto sem necessidade
  imediata. Chamada direta a um método `@Async` é suficiente e consistente
  com a arquitetura atual.

## Arquivos afetados (resumo)

| Arquivo | Mudança |
|---|---|
| `infra/config/AsyncConfig.java` (novo) | `@EnableAsync`, `ThreadPoolTaskExecutor`, `AsyncUncaughtExceptionHandler` |
| `infra/services/EmailService.java` | `sendMail` vira `@Async`, retorna `CompletableFuture<String>`; `catch (ResendException e)` retorna `CompletableFuture.failedFuture(e)` em vez de relançar |
| `services/AuthService.java` | `sendVerificationEmail` anexa `.exceptionally(...)` na future retornada por `sendMail`; adicionar `@Slf4j` |

## Verificação sugerida

1. `./mvnw compile` para validar as novas anotações/imports
   (`spring-boot-starter` já traz `@EnableAsync`/`ThreadPoolTaskExecutor`,
   nenhuma dependência nova é necessária).
2. Medir o tempo de resposta de `POST /auth/register` antes/depois —
   deve cair para próximo do tempo de persistir o usuário no banco,
   independente da latência do Resend (pode simular Resend lento/fora do
   ar, ex. apontando para um host que não responde, e confirmar que a
   resposta HTTP não trava).
3. Confirmar que o usuário é criado e a resposta 201 volta mesmo com o
   Resend falhando, e que o erro aparece nos logs do servidor (ainda que
   depois da resposta já ter sido enviada ao cliente).
4. Registrar múltiplos usuários em sequência rápida e confirmar que a pool
   de threads (`email-async-*`) não cresce de forma descontrolada nem gera
   thread leak.
