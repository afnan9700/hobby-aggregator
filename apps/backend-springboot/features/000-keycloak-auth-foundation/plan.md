# Plan: Keycloak Auth Foundation

## Prerequisites
- [x] Approved spec exists at `apps/backend-springboot/features/000-keycloak-auth-foundation/spec.md`
- [x] Target module is `apps/backend-springboot`
- [x] Current backend baseline is limited to app bootstrap, PostgreSQL wiring, Docker packaging, and one context-load test

## Files to Read
- `AGENTS.md`
- `apps/backend-springboot/AGENTS.md`
- `.agents/docs/conventions/spring-boot.md`
- `apps/backend-springboot/features/000-keycloak-auth-foundation/spec.md`
- `apps/backend-springboot/app-state.md`
- `apps/backend-springboot/pom.xml`
- `apps/backend-springboot/src/main/resources/application.yaml`
- `apps/docker-compose.yml`
- `apps/.env`
- `apps/backend-springboot/.env`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/HobbyAggregatorApplication.java`
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/HobbyaggregatorApplicationTests.java`

## Steps

### Step 1: Add explicit auth configuration and dependency scaffolding
**Goal**: Make the backend configuration-ready for Keycloak-issued JWT validation and role mapping without introducing endpoint behavior yet.
**Files to create**:
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/AuthProperties.java`
**Files to modify**:
- `apps/backend-springboot/pom.xml`
- `apps/backend-springboot/src/main/resources/application.yaml`
**Implementation notes**:
- Keep the backend as a stateless resource server and wire issuer-based JWT validation through Spring Security configuration properties.
- Add app-owned configuration properties for Keycloak realm name, backend client ID, and the role names the app will recognize for `ADMIN` and `MODERATOR`.
- Add environment-backed properties for `spring.security.oauth2.resourceserver.jwt.issuer-uri` so local direct runs and Compose runs can point at different Keycloak URLs without code changes.
- Add logging categories for authentication failures, authorization denials, and user-provisioning events, while explicitly avoiding token or secret logging.
- Review `pom.xml` for any missing test/runtime dependencies needed for security configuration and container-based tests, but do not add frontend-login or gateway-oriented dependencies.
**Verification**:
- [ ] `./mvnw -q -DskipTests compile` succeeds with the new properties classes and config placeholders
- [ ] The application still starts when the issuer URI is supplied through environment variables
**Rollback**:
- Revert the commit that introduces the auth property class, POM changes, and YAML configuration entries for this step

### Step 2: Add the app-owned user profile model and provisioning service
**Goal**: Create the minimal persistence layer that mirrors Keycloak identity through `sub` while keeping credentials out of the app database.
**Files to create**:
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/user/domain/AppUser.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/user/domain/AppUserRepository.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/user/application/AppUserService.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/user/api/AppUserResponse.java`
**Files to modify**:
- `apps/backend-springboot/src/main/resources/application.yaml`
**Implementation notes**:
- Model the `app_user` table with an internal primary key, unique `keycloak_subject`, optional mirrored `email`, optional mirrored `display_name`, optional `avatar_object_key`, and created/updated timestamps.
- Keep the JPA entity internal to the persistence layer and expose DTOs from controllers instead of returning entities directly.
- Implement service logic that provisions a new `AppUser` on first authenticated request and updates mirrored fields when the token carries newer values.
- Prefer `sub` as the immutable linkage key, with `email`, `preferred_username`, and `name` treated as mirrors only.
- Keep transaction boundaries in the service layer so user creation or updates happen atomically when `/api/me` is called.
**Verification**:
- [ ] Repository/service tests confirm unique lookup by `keycloak_subject`
- [ ] Provisioning logic creates a row for a first-time subject and reuses the same row on later requests
**Rollback**:
- Revert the commit that introduces the `AppUser` domain model, repository, DTO, and provisioning service

### Step 3: Configure stateless security and Keycloak role mapping
**Goal**: Enforce `401` and `403` behavior correctly and translate Keycloak token claims into Spring authorities the app can reason about.
**Files to create**:
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/SecurityConfig.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/KeycloakJwtAuthoritiesConverter.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/RestAuthenticationEntryPoint.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/RestAccessDeniedHandler.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/AuthenticatedUser.java`
**Files to modify**:
- `apps/backend-springboot/src/main/resources/application.yaml`
**Implementation notes**:
- Configure a `SecurityFilterChain` with CSRF disabled for the API, stateless session handling, and path-based authorization rules.
- Keep `GET /api/health` public, require authentication for all other `/api/**` routes, require `ADMIN` for `/api/admin/**`, and reserve a matcher for future `/api/moderation/**`.
- Implement a JWT authorities converter that can read Keycloak `realm_access.roles` and optionally `resource_access.<client-id>.roles`, then normalize them into Spring authorities such as `ROLE_ADMIN` and `ROLE_MODERATOR`.
- Add explicit REST-friendly handlers so authentication failures return `401` and access denials return `403` with clean JSON responses and SLF4J logging.
- Introduce a small authenticated-user abstraction so controllers and services do not need to parse raw JWT claims repeatedly.
**Verification**:
- [ ] Security slice tests show unauthenticated access to protected routes returns `401`
- [ ] Mock JWT tests show authenticated non-admin access to `/api/admin/**` returns `403`
- [ ] Mock JWT tests show admin access reaches the handler successfully
**Rollback**:
- Revert the commit that introduces the security configuration, JWT converter, and REST exception handlers

### Step 4: Add the v1 API surface for health, self-profile, and admin protection
**Goal**: Expose the first API endpoints required by the spec while keeping controllers thin and authorization explicit at the application boundary.
**Files to create**:
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/health/api/HealthController.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/me/api/MeController.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/me/api/MeResponse.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/admin/api/AdminController.java`
**Files to modify**:
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/user/application/AppUserService.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/security/SecurityConfig.java`
**Implementation notes**:
- Implement `GET /api/health` as a minimal public JSON response and do not expose the full actuator surface publicly.
- Implement `GET /api/me` to return authenticated identity plus the app-owned profile row, provisioning the user lazily through `AppUserService` when the subject is first seen.
- Keep the `GET /api/me` response free of token values and avoid echoing raw role claims back to the client.
- Implement `GET /api/admin/ping` as a simple admin-only endpoint used purely to verify authorization wiring.
- Keep controller logic small and delegate data assembly or provisioning work to application services.
**Verification**:
- [ ] MVC tests cover `GET /api/health` as public
- [ ] MVC tests cover `GET /api/me` as `401` when unauthenticated and `200` when a valid JWT is present
- [ ] MVC tests cover `GET /api/admin/ping` as `403` for ordinary users and `200` for admins
**Rollback**:
- Revert the commit that introduces the controllers and `/api/me` response contract

### Step 5: Extend local Docker Compose with Keycloak, realm import, and environment wiring
**Goal**: Make local development fully containerized so the backend, Keycloak, and both required databases can start together reproducibly.
**Files to create**:
- `apps/backend-springboot/docker/keycloak/realm-import/hobby-aggregator-realm.json`
**Files to modify**:
- `apps/docker-compose.yml`
- `apps/.env`
- `apps/backend-springboot/.env`
- `apps/backend-springboot/src/main/resources/application.yaml`
**Implementation notes**:
- Add a dedicated `keycloak-db` PostgreSQL service so the app database and identity-provider database are isolated.
- Add a `keycloak` service using an explicit image tag, healthcheck, startup import command, and a volume mount for the checked-in realm export.
- Seed the imported realm with the backend client, realm or client roles for `ADMIN` and `MODERATOR`, and at least one ordinary user plus one admin user for manual and automated validation.
- Wire backend environment variables so Compose uses the internal issuer URL and direct local runs use a localhost issuer URL.
- Leave room in env/config for later SMTP and social-login configuration, but keep the backend code agnostic because Keycloak owns those flows.
**Verification**:
- [ ] `docker compose up --build` starts `db`, `keycloak-db`, `keycloak`, and `backend` successfully
- [ ] A token obtained from the seeded Keycloak user can call `GET /api/me`
- [ ] The seeded admin token succeeds against `GET /api/admin/ping`
**Rollback**:
- Revert the commit that introduces Keycloak Compose services, env wiring, and the realm import file

### Step 6: Add focused unit and slice tests for the risky auth behavior
**Goal**: Cover the highest-value auth rules cheaply before relying on the slower container-based path.
**Files to create**:
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/security/KeycloakJwtAuthoritiesConverterTest.java`
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/user/application/AppUserServiceTest.java`
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/api/ApiSecurityMvcTest.java`
**Files to modify**:
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/HobbyaggregatorApplicationTests.java`
**Implementation notes**:
- Test the JWT converter against representative Keycloak claim shapes so role mapping does not regress silently.
- Test user provisioning and mirror updates independently from the web layer.
- Use Spring Security test support with mocked JWTs to verify the public, authenticated, and admin-only route behavior.
- Either narrow the existing context-load test to the final configuration or replace it with more targeted coverage so it continues to add value.
**Verification**:
- [ ] `./mvnw test -Dtest=KeycloakJwtAuthoritiesConverterTest,AppUserServiceTest,ApiSecurityMvcTest` succeeds
- [ ] The tests fail if role mapping or route protection is broken
**Rollback**:
- Revert the commit that introduces the unit and slice tests for auth behavior

### Step 7: Add one real auth-path integration test with Testcontainers
**Goal**: Satisfy the acceptance criterion that at least one integration test exercises the real Keycloak-to-backend-to-database path.
**Files to create**:
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/integration/AuthIntegrationTest.java`
- `apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/integration/KeycloakContainerSupport.java`
**Files to modify**:
- `apps/backend-springboot/pom.xml`
- `apps/backend-springboot/src/main/resources/application.yaml`
**Implementation notes**:
- Start PostgreSQL and Keycloak containers in the test suite and inject datasource and issuer settings dynamically into the Spring Boot test context.
- Reuse the checked-in realm import so local Compose and automated tests validate the same roles, users, and client metadata.
- Obtain real access tokens from Keycloak for the seeded ordinary user and admin user, then verify `/api/me` success, lazy app-user provisioning, and `/api/admin/ping` role enforcement.
- Keep this test focused on the end-to-end auth path so it remains stable and worth its runtime cost.
- If direct token acquisition requires a Keycloak-specific test client configuration, isolate that to the test realm export rather than changing the production backend security model.
**Verification**:
- [ ] `./mvnw verify` succeeds with the containerized integration test enabled
- [ ] The integration test proves `401`, `403`, and successful authenticated access against real infrastructure
**Rollback**:
- Revert the commit that introduces container integration support and the real auth-path test

## Risks and Open Questions
- Decide once, then standardize everywhere, whether the app will read Keycloak realm roles only or both realm and client roles. The plan above supports both, but the realm export and tests should choose a single canonical pattern.
- Pick and pin a specific Keycloak image version before implementation so Compose and Testcontainers run against the same server behavior.
- Decide how much SMTP scaffolding belongs in this feature. The backend does not need email logic, but env placeholders and documentation may still be useful because Keycloak-hosted verification and reset flows depend on it later.

## Testing
- Unit tests:
  - JWT authority conversion from Keycloak claims
  - `AppUserService` provisioning and mirror updates
- Integration tests:
  - One Testcontainers test that starts PostgreSQL and Keycloak, obtains real tokens, calls `/api/me`, and verifies `/api/admin/ping` for both ordinary and admin users
- Manual checks:
  - Start the full stack with `docker compose up --build`
  - Log in through the seeded Keycloak realm or token endpoint and call `/api/me`
  - Verify an unauthenticated request returns `401`
  - Verify an authenticated non-admin request to `/api/admin/ping` returns `403`

## Overall Rollback Plan
1. Revert the implementation commit for the security and persistence scaffolding if the auth model proves incorrect.
2. Revert the Compose and realm-import commit if the local infrastructure setup blocks development.
3. Revert the test-suite commit if the container strategy is unstable, then reintroduce the tests after the infrastructure contract is simplified.
4. Keep the rollback sequence in commit-sized units so config, code, and test changes can be backed out independently.
