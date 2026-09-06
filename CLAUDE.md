# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Barbup API is a Spring Boot 4.1.0 (Java 25) REST backend for a barbershop management/booking product. It uses Maven, Spring Data JPA (PostgreSQL, via Supabase), Spring Security with stateless JWT auth, and springdoc-openapi (JSON generation only) with a static Scalar page for API docs.

Note: the Maven `groupId`/base package is `com.barbup.barbup_api` (underscore) instead of `com.barbup.barbup-api`, because hyphens are invalid in Java package names — this is intentional, not a typo, per `HELP.md`.

## Commands

All commands use the Maven wrapper (no local Maven install required).

```
./mvnw compile                      # compile
./mvnw test                         # run all tests
./mvnw test -Dtest=ClassName                    # run a single test class
./mvnw test -Dtest=ClassName#methodName         # run a single test method
./mvnw spring-boot:run                          # run the app (uses application.properties)
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
                                                # run with local profile (application-local.properties)
./mvnw clean package                # build the jar
```

On Windows use `mvnw.cmd` instead of `./mvnw` when not in a POSIX shell.

There is no linter configured in this repo.

## Local Development Setup

Create `src/main/resources/application-local.properties` with local values (sample provided in `application-local.properties`). Before running locally:

1. **PostgreSQL**: Start a local instance (`docker run ... postgres`) or use an existing one.
2. **Run**: `./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"`
3. **Flyway** will auto-migrate the schema on startup using scripts in `src/main/resources/db/migration/`.

`application.properties` holds prod/staging config with env-var placeholders; `application-local.properties` has hardcoded dev values (committed, safe).

## Architecture

### Package layout (`src/main/java/com/barbup/barbup_api/`)

- `controllers/` — `@RestController` classes, one per resource (`AuthController`, `BarbershopController`, `UserController`). Controllers are thin: they validate input via `@Validated` DTOs and delegate to a `services/` class.
- `services/` — business logic. Services are `@Autowired`-field-injected (not constructor injection), except controllers which use `@RequiredArgsConstructor` on `final` fields.
- `domain/` — JPA entities and their DTOs, grouped by aggregate:
  - `domain/abstracts/BaseEntity` — mapped superclass all entities extend. Provides UUID string `id`, `active` soft-delete flag, and Spring Data JPA auditing fields (`createdAt`/`updatedAt`/`createdBy`/`updatedBy`).
  - `domain/user/` — `User` (implements Spring Security `UserDetails`), `UserRole` enum (ADMIN/USER), DTOs.
  - `domain/barbershop/` — `Barbershop` entity (has an `owner` -> `User`), and nested aggregates: `member/` (`Member`, `MemberRole`), `schedule/` (`BusinessHours`, `BarberSchedule`, `BarberTimeOff`), `appointment/` (`Appointment`, `AppointmentStatus`, `AppointmentSource`), `service/` (`Service`).
  - Each DTO subpackage (`dto/`) holds request/response records for that aggregate; DTOs use `jakarta.validation` annotations (`@NotBlank`, `@NotNull`) and controllers validate them with `@Validated`.
- `repositories/` — plain `JpaRepository<Entity, String>` interfaces (String id, matching `BaseEntity`'s UUID string PK). Custom query methods (e.g. `UserRepository.findByEmail`) live here. **Important:** Queries use JPQL (entity/property names) not SQL. Use `@Query` with entity names (`PasswordResetCode`) and attribute names (`c.consumedAt`); for relationships, traverse with dot notation (`c.user.id`). Only use `nativeQuery=true` for DB-specific features.
- `infra/security/` — JWT auth stack:
  - `TokenService` issues/validates HS256 JWTs (issuer `barbup-api`, subject = user email) using `api.security.token.secret` from `application.properties`.
  - `SecurityFilter` (`OncePerRequestFilter`) reads the `Authorization: Bearer <token>` header, validates it via `TokenService`, loads the `User` by email, and sets it in `SecurityContextHolder`.
  - `SecurityConfig` wires the filter chain: CSRF disabled, stateless sessions, `POST /auth/login`, `POST /auth/register`, `/v3/api-docs/**` and `/scalar.html` are public, everything else requires authentication.
  - `AuditorAwareImpl` supplies `createdBy`/`updatedBy` for JPA auditing from the current `SecurityContextHolder` authentication (email).
- `infra/cors/CorsConfig` — global CORS mapping (currently allows `http://localhost:3000`, GET/POST only).
- `infra/event/` — Spring application events (`PasswordResetRequestedEvent`, `PasswordChangedEvent`, etc.). Services publish events via `ApplicationEventPublisher`; event handlers use `@EventListener` methods (e.g., `EmailEventListener` sends emails when events fire).
- `infra/mappers/` — MapStruct `@Mapper` classes for DTO ↔ Entity conversions. Warnings about unmapped target properties are normal for BaseEntity fields (`id`, `createdAt`, etc.) that come from the database, not the DTO.
- `infra/persistence/` — repositories moved here from `repositories/` (refactored into `infra/`).
- `infra/mappers/` — DTO mappers (e.g. `BarbershopMapper`).
- `exception/` — `GlobalExceptionHandler` (`@RestControllerAdvice`) centralizes error responses (validation errors, `EntityNotFoundException`, `AccessDeniedException`, `BadCredentialsException`, `UsernameNotFoundException`, custom `EmailAlreadyExistsException`, malformed JSON, and a generic 500 fallback). Add new domain exceptions here rather than handling errors ad hoc in controllers.

### Entity conventions

- All entities extend `BaseEntity` and get a UUID `id`, soft-delete (`active` boolean + `@SQLRestriction("active = true")` so deleted rows are auto-filtered from queries), and auditing timestamps/users. Call `entity.delete()` (sets `active = false`) instead of hard-deleting.
- Entities use Lombok `@Getter @Setter @AllArgsConstructor @NoArgsConstructor`; keep that pattern for new entities.
- **Flyway migrations** (`src/main/resources/db/migration/`) manage schema changes. When adding an entity, create a new migration file `VN__description.sql` (e.g. `V3__create_password_reset_tokens_table.sql`). Flyway auto-runs migrations on startup. `spring.jpa.hibernate.ddl-auto=validate` ensures entities match the schema.

### Auth flow

1. `POST /auth/register` creates a `User` with `UserRole.USER`, BCrypt-hashed password.
2. `POST /auth/login` authenticates via Spring Security's `AuthenticationManager`, then `TokenService` issues a JWT (2h expiry).
3. Subsequent requests send `Authorization: Bearer <token>`; `SecurityFilter` resolves it to a `User` principal, injectable into controllers via `@AuthenticationPrincipal User`.
4. Barbershop creation (`BarbershopService.createBarbershop`) also creates a `Member` row linking the owning `User` to the new `Barbershop` with `MemberRole.OWNER` — barbershop membership (not just `Barbershop.owner`) is the source of truth for who belongs to a shop and with what role.

### Config

- `src/main/resources/application.properties` — prod/staging config with env-var placeholders (`${DB_URL}`, `${JWT_SECRET}`, etc.). Never commit real secrets here.
- `src/main/resources/application-local.properties` — local dev config with hardcoded values (safe to commit; used only locally via `--spring.profiles.active=local`).
- Spring profiles: the `application-{profile}.properties` file is loaded in addition to `application.properties` when `--spring.profiles.active={profile}` is set. Use `local` for development, `prod`/`staging` for deployments.
