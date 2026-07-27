The app has been bootstrapped using Spring Initializr and extended with the following:

- **Global exception handler**: `@ControllerAdvice` with consistent JSON error responses, SLF4J logging, and domain-specific exceptions (`ResourceNotFoundException`, `DuplicateResourceException`).
- **Docker packaging**: Multi-stage `Dockerfile` using `eclipse-temurin:21-jdk-alpine` for build and `eclipse-temurin:21-jre-alpine` for runtime.
- **Docker Compose**: PostgreSQL 17 service with healthcheck, backend service wired to it.
- **JPA + PostgreSQL**: `application.yaml` configured with datasource, Hibernate dialect, and `open-in-view: false`.
- **Environment files**: `.env` (gitignored) and `.env.example` for both root (PostgreSQL) and backend (Spring Boot).