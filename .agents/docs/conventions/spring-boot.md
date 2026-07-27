# Spring Boot Conventions

## Structure

* Keep controllers thin.
* Put business logic in services.
* Repositories handle persistence only.
* Use DTOs for API boundaries.
* Keep DTOs separate from entities.
* Never expose entities directly.

## Dependency Injection

* Use constructor injection only.
* Avoid field injection.
* Keep beans small and focused.

## Persistence

* Use JPA for data access.
* Define clear transaction boundaries.
* Avoid lazy-loading in web layer.
* Prefer simple, explicit mappings.
* Use transactions deliberately.

## Exception Handling

* Use `GlobalExceptionHandler` (`common` package) — the single `@ControllerAdvice` for all API errors.
* Never expose stack traces or internal errors.
* Prefer domain-specific exceptions (`ResourceNotFoundException` → 404, `DuplicateResourceException` → 409).
* Extend the handler with new `@ExceptionHandler` methods for app-specific exceptions as needed.
* Map exceptions to consistent HTTP responses.

### Error Format

All errors follow:

```json
{
  "timestamp": "ISO-8601",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/example/123"
}
```

### Rules

* Validate early → return 400.
* Use 409 for business conflicts.
* Log full details internally, return sanitized messages externally.

## Logging

* Use SLF4J.
* Log meaningful events only.
* Never log secrets or sensitive data.

## Build & Runtime

* Follow Maven conventions.
* Keep Docker setup minimal and reproducible.
* Use environment variables and profiles for config.

## General

* Prefer readability over cleverness.
* Keep changes small and focused.
* Add tests only when behavior matters.
