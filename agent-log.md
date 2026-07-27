# Agent Log

This file is an audit trail for the project owner. Append entries after meaningful work sessions.

## 2026-07-27
**Task**: Add global exception handler, Docker packaging, PostgreSQL, and JPA configuration

**Steps completed**:
- Created `common` package with `GlobalExceptionHandler`, `ErrorResponse`, `ResourceNotFoundException`, `DuplicateResourceException`
- Added Dockerfile for multi-stage Spring Boot build
- Added docker-compose.yml with PostgreSQL 17 and backend service
- Added .env files for PostgreSQL and backend configuration
- Added .env.example files for reference
- Updated .gitignore to exclude .env files
- Added .dockerignore for efficient Docker builds
- Configured application.yaml with JPA/PostgreSQL connection properties
- Verified compilation and existing tests pass

**Files created**:
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/common/ErrorResponse.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/common/GlobalExceptionHandler.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/common/ResourceNotFoundException.java`
- `apps/backend-springboot/src/main/java/com/somedomain/hobbyaggregator/common/DuplicateResourceException.java`
- `apps/backend-springboot/Dockerfile`
- `apps/backend-springboot/.dockerignore`
- `apps/backend-springboot/.env`
- `apps/backend-springboot/.env.example`
- `docker-compose.yml`
- `.env`
- `.env.example`

**Files modified**:
- `apps/backend-springboot/src/main/resources/application.yaml`
- `.gitignore`
- `apps/backend-springboot/.gitignore`
- `agent-log.md`

**Decisions made**:
- Validation errors return 400 with field-level details
- Resource not found returns 404 via `ResourceNotFoundException`
- Duplicate resources return 409 via `DuplicateResourceException`
- All unhandled errors return 500 with sanitized message (no stack traces)
- `open-in-view` is disabled to avoid lazy-loading pitfalls in web layer
- PostgreSQL healthcheck in docker-compose ensures backend starts only after DB is ready
- `.env` files are gitignored; `.env.example` files serve as documentation

**Issues**:
- None

**Next**:
- Keycloak auth foundation feature