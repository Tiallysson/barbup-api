# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Barbup API is a Spring Boot 4.1.0 (Java 25) REST backend for a barbershop management/booking product. Maven, Spring Data JPA on PostgreSQL (Supabase), Flyway, Spring Security with stateless JWT (`com.auth0:java-jwt`), MapStruct, Thymeleaf (email templates only), Bucket4j + Caffeine (rate limiting), and springdoc-openapi (JSON only) served through a static Scalar page at `/scalar.html`.

The base package is `com.barbup.barbup_api` (underscore) because hyphens are invalid in Java package names — intentional, not a typo.

## Commands

```
./mvnw compile
./mvnw test
./mvnw test -Dtest=ClassName
./mvnw test -Dtest=ClassName#methodName
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
./mvnw clean package
```

On Windows use `mvnw.cmd` outside a POSIX shell. No linter is configured. The only test is `BarbupApiApplicationTests.contextLoads` (`@SpringBootTest`), which needs a reachable database and all required properties to pass. The `Dockerfile` builds with `-DskipTests`.

## Configuration

- `application.properties` (tracked) — env-var placeholders: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `RESEND_API_KEY`, `MAILJET_API_KEY`, `MAILJET_API_SECRET`, `MAILJET_SENDER_EMAIL`, `EMAIL_PROVIDER` (default `mailjet`), `CORS_ALLOWED_ORIGINS` (comma-separated).
- `application-local.properties` — **gitignored and contains real credentials**; loaded on top of `application.properties` with the `local` profile. Never commit it or copy its values elsewhere. It does not define `app.cors.allowed-origins`, so `CORS_ALLOWED_ORIGINS` must be set in the environment (or added to the local file) for the app to start.
- `.gitignore` ignores `*.properties`; a new tracked properties file needs `git add -f`.
- Schema is owned by Flyway (`src/main/resources/db/migration/VN__description.sql`) with `ddl-auto=validate`. Any entity change needs a new migration; never edit an applied one. Timestamps are `Instant`/`timestamptz` in UTC.

## Architecture

Package layout under `src/main/java/com/barbup/barbup_api/`:

- `controllers/` — thin `@RestController`s grouped by audience, each with its own URL prefix: `auth/` (`/auth/**`, public), `account/` (`/me`, any logged-in user), `client/` (`/client/barbershops/{barbershopId}/...`, read-only data customers need), `manage/` (`/manage/barbershops/**`, shop staff). `/admin/**` is reserved for `UserRole.ADMIN`. Shop-scoped IDs travel in the path (`{barbershopId}`, `{serviceId}`, `{hourId}`), never in request bodies. Validate DTOs with `@Valid`; get the caller via `@AuthenticationPrincipal User`.
- `services/` — business logic and **authorization**. Constructor injection only: `private final` fields + Lombok `@RequiredArgsConstructor` (or an explicit constructor), never field `@Autowired`. In `@Configuration` classes take dependencies and `@Value`s as `@Bean` method parameters.
- `domain/abstracts/BaseEntity` + `domain/entity/<aggregate>/` — JPA entities (`user`, `barbershop`, `member`, `schedule`, `appointment`, `service`, `address`, `password`). Entities use Lombok `@Getter @Setter @AllArgsConstructor @NoArgsConstructor`.
- `shared/dto/<area>/` — request/response records with `jakarta.validation` annotations. `shared/exception/` — custom domain exceptions.
- `exception/GlobalExceptionHandler` — single `@RestControllerAdvice` mapping exceptions to status codes and `ErrorResponseDTO`. New domain exceptions go in `shared/exception/` and get a handler here.
- `infra/persistence/` — Spring Data repositories, `JpaRepository<Entity, UUID>`. Custom queries use JPQL (entity/attribute names, dot traversal like `c.user.id`); `nativeQuery = true` only for DB-specific features.
- `infra/mappers/` — MapStruct mappers (`componentModel = "spring"`). Unmapped-target warnings for `BaseEntity` fields are expected.
- `infra/security/`, `infra/ratelimit/`, `infra/event/`, `infra/email/` — see below.

### Entities

`BaseEntity` gives every entity a UUIDv7 `UUID id`, an `active` soft-delete flag with `@SQLRestriction("active = true")` (inactive rows disappear from all JPA queries), and auditing (`createdAt`/`updatedAt`/`createdBy`/`updatedBy`). Auditing is enabled via `@EnableJpaAuditing(auditorAwareRef = "auditorAware")`; `AuditorAwareImpl` uses the authenticated email. Soft-delete with `entity.delete()` instead of repository deletes.

### Authentication

- `TokenService` issues/validates HS256 JWTs (issuer `barbup-api`, subject = email, 2h TTL).
- `SecurityFilter` reads `Authorization: Bearer`, loads the `User` by email and puts it in the `SecurityContext`. `User` implements `UserDetails`; `isEnabled()` is `emailVerified`, so unverified users fail login with `DisabledException` (403).
- `SecurityConfig`: stateless, CSRF off, 401 entry point, `@EnableMethodSecurity`. The public `POST /auth/*` endpoints (login, register, confirm-email, verify-code, forgot-password, reset-password, resend-verification) plus `/scalar.html` and `/v3/api-docs/**` are whitelisted; `/admin/**` requires `ROLE_ADMIN`; `/manage/barbershops/{barbershopId}/**` goes through `BarbershopMemberAuthorizationManager` (caller must be a `Member` of that shop, invalid UUID → 403); everything else requires auth. Matcher order matters — add rules before `anyRequest()`. CORS is the `corsConfigurationSource` bean in this class (origins from `app.cors.allowed-origins`).
- `AuthRateLimitFilter` runs before the security filter and rate-limits the public auth POSTs per IP using `RateLimitPlan`. New public auth endpoints must be added both to the `SecurityConfig` whitelist and to `PLANS` here.

### Authorization (barbershop membership)

`UserRole` (USER/ADMIN) is platform-level only. Per-shop permissions come from `Member` (`barbershop_member`: user + barbershop + `MemberRole` OWNER/BARBER/RECEPTIONIST), which is the source of truth for who belongs to a shop. Creating a barbershop also creates the OWNER `Member`.

Authorization has three layers:
1. URL: the `/manage/barbershops/{barbershopId}/**` membership gate in `SecurityConfig` (any `MemberRole`).
2. Method: `@PreAuthorize` on **service methods** against the `barbershopAccess` bean (`infra/security/BarbershopAccess`) for the specific `MemberRole`, e.g. `@PreAuthorize("@barbershopAccess.canManage(#barbershopId, principal)")` (OWNER only). Service methods take `barbershopId` as a parameter for this.
3. Ownership: load shop-owned resources with `findByIdAndBarbershopId(id, barbershopId)` so an ID from another shop yields 404.

`BarbershopAccess` queries `MemberRepository` on every call, so membership changes take effect immediately. Don't put shop roles in the JWT.

### Events and email

Services publish events (`infra/event/*Event`) via `ApplicationEventPublisher`; listeners in `infra/event/listener/` are `@Async @EventListener` and render Thymeleaf templates from `src/main/resources/templates/email/` through `EmailTemplateRenderer`, then send via `EmailService`. The implementation is chosen by `app.email.provider` (`MailjetEmailService` by default, `ResendEmailService` when `resend`) with `@ConditionalOnProperty`. Listeners catch and log send failures, so email errors don't fail the request.
