# Spec: Keycloak Auth Foundation

## Overview

This feature sets up authentication and coarse authorization for `hobby-aggregator/` using Keycloak as the identity provider and Spring Boot as the application backend/resource server. Keycloak is the system that handles sign-up, login, Google login, password reset, email verification, and account management; Spring Boot should trust Keycloak-issued tokens and enforce app-specific access rules. Keycloak supports identity brokering/social login, centralized user and session management, and standard account flows, while Spring Security can validate JWT bearer tokens as a resource server and can also support OAuth2 login later if the architecture ever needs it.

The design goal is not to build a custom auth system. The design goal is to use Keycloak for identity and Spring Security for authorization, while keeping the app ready for future features such as admin routes, moderation, premium tiers, avatar storage, and future external providers. This makes the auth layer production-shaped from the beginning without forcing the app to own password storage or social-login plumbing itself. Keycloak’s admin console is the place where identity brokering, users, sessions, and fine-grained authorization policies are centrally managed.

## Goals

* Use Keycloak for registration, login, logout, password reset, email verification, and Google login.
* Use Spring Boot only as the API/resource server and application authorization layer.
* Support future app roles such as admin and moderator without redesigning auth.
* Leave room for premium-tier access checks, profile pictures, and future identity providers.
* Keep the local development setup fully containerized from day one.
* Add testing early enough that auth regressions are caught before merge.

## Non-Goals

* Building a custom password database in Spring Boot.
* Building a custom OAuth/OpenID Connect provider.
* Building an API gateway in v1.
* Building premium billing/subscription payments in v1.
* Building moderation tools beyond the authorization scaffolding.
* Building a custom UI for all login/account screens in Spring Boot.

## Acceptance Criteria

* [ ] A Docker Compose environment can start Keycloak, its database, and the Spring Boot app together.
* [ ] Spring Boot can validate access tokens issued by Keycloak.
* [ ] Unauthenticated requests to protected endpoints return `401`.
* [ ] Requests with insufficient roles return `403`.
* [ ] A user can log in through Keycloak and call a protected `GET /api/me`-style endpoint successfully.
* [ ] Admin-only endpoints are inaccessible to ordinary users.
* [ ] The app has a place to store the Keycloak user subject (`sub`) and minimal application profile data.
* [ ] The configuration is ready for adding Google login, verification, and forgot-password flows later without rewriting the backend security model.
* [ ] At least one integration test covers the real auth path using containers.

## Technical Scope

### In Scope

* Keycloak realm configuration for the application.
* A dedicated OIDC client for the Spring Boot backend.
* Spring Security resource server configuration using Keycloak-issued JWTs.
* Authority mapping from token claims into Spring `GrantedAuthority` values.
* App-side route protection for user, moderator, and admin paths.
* Minimal local user profile persistence keyed by the Keycloak subject.
* Docker Compose for Keycloak, the auth database, and the backend.
* Testcontainers-based integration tests for auth and database behavior.
* SLF4J logging for auth events, failures, and suspicious access attempts.
* Hibernate/JPA for the small amount of app-owned profile data.

Spring Boot’s resource server support can derive the JWK set location from the issuer metadata automatically, and Spring Security exposes a JWT-to-authorities conversion hook so the backend can map Keycloak roles into application authorities cleanly. Spring Boot also supports Docker Compose and Testcontainers as first-class development-time services, which fits this setup well.

### Out of Scope

* Local password hashing and credential storage.
* A self-hosted auth UX inside Spring Boot for sign-up/login screens.
* An API gateway.
* Fine-grained per-object moderation rules beyond basic authorization scaffolding.
* Payment integration and plan management.
* Multiple realms or multi-tenant auth.
* Storing avatars or media inside the relational database.

## Edge Cases

1. Token expired or signature invalid: the API should reject the request with `401`.
2. User exists in Keycloak but not yet in the app DB: create the app profile lazily on first successful login or first authenticated request.
3. User has valid login but lacks the expected role: return `403`, not `401`.
4. Role assignment changes in Keycloak are not reflected until the next token refresh or next login.
5. Keycloak or its database is unavailable locally: startup should fail fast or log a clear dependency error.
6. Email verification or forgot-password flows are enabled in Keycloak but SMTP is misconfigured: surface the failure clearly in logs and documentation.
7. Google login is configured later: no Spring code should be required beyond token validation, because Keycloak handles identity brokering and social login centrally.

## Dependencies

* Requires: Keycloak server, a Keycloak database, Spring Boot security configuration, and Docker Compose.
* Requires: SMTP service settings for verification and password-reset email flows.
* Requires: a future frontend or API client that can obtain Keycloak tokens.
* Blocks: admin tools, moderation actions, premium-gated features, and any app feature that needs authenticated identity.

## Data Model

Keep auth credentials out of the app database.

The app database should store only app-owned identity mirrors and metadata:

* `app_user`

  * `id` (internal primary key)
  * `keycloak_subject` (unique, from the OIDC token `sub`)
  * `email` (optional mirror)
  * `display_name` (optional mirror)
  * `avatar_object_key` (optional, future use)
  * `created_at`
  * `updated_at`

Optional later tables:

* `user_entitlement` for premium flags or subscription state.
* `audit_event` for important auth and admin actions.
* `user_linked_source` for external hobby platform connections later.

Do not store passwords, password hashes, reset tokens, or OAuth secrets in the app database.

## API Surface

### Required in v1

* `GET /api/me`

  * Returns the authenticated user identity and the app profile row.
* `GET /api/admin/ping`

  * Minimal admin-only route to verify role protection works.
* `GET /api/health`

  * Public health check for Docker and deployment checks.

### Security behavior

* All non-public API routes require authentication.
* `/api/admin/**` requires an admin authority.
* Future `/api/moderation/**` will require moderator or admin authority.
* Future premium routes should check a subscription/entitlement flag in the app DB, not a hard-coded auth role.

## UI / UX Notes

* Login, registration, Google login, password reset, and email verification should be Keycloak-hosted for v1.
* The Spring Boot app should not build a custom login form in the first iteration.
* If the frontend later becomes a SPA, use the backend only as an API resource server and keep the login flow in Keycloak.
* Error states should distinguish clearly between unauthenticated, unauthorized, and forbidden.
* Public pages should never show auth tokens or role names directly.

## Testing Strategy

Use a three-layer test approach.

1. Fast unit tests for pure business logic.
2. Slice tests for controllers, security rules, and repository behavior.
3. One or more integration tests using real containers for Keycloak and the database.

The cheapest useful version is to write tests only for the high-risk parts first: login acceptance, protected route rejection, admin-only route success/failure, and app-user provisioning from the token subject. Spring Boot’s test slices and Testcontainers are a good fit here because they let you test against real infrastructure without hand-maintaining local services.

## Implementation Notes

* Use Keycloak realm roles or client roles for broad categories like `ADMIN` and `MODERATOR`.
* Keep premium as an app entitlement or plan state unless you have a strong reason to model it as a role.
* Map token authorities into Spring Security authorities with a custom JWT converter if the default claim mapping is not enough.
* Keep auth logs in SLF4J with enough context to debug failures, but never log secrets or access tokens.
* Use Docker Compose for local development so the auth stack is reproducible.
* Export Keycloak realm configuration into the repo so the setup can be recreated consistently.
* Do not add an API gateway yet; it does not buy much while the system is still just Keycloak plus one backend.

## Files to Read

* `/apps/backend-springboot/pom.xml`
* `/apps/backend-springboot/src/main/resources/application.yml`
* `/apps/docker-compose.yml`
* `/apps/backend-springboot/app-state.md`

## Files to Read only if Needed

* `/apps/backend-springboot/src/test/java/com/somedomain/hobbyaggregator/HobbyaggregatorApplicationTests.java`