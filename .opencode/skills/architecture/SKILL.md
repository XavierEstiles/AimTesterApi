---
name: architecture
description: >
  Use when making architectural decisions, adding new modules,
  refactoring code, introducing dependencies or modifying application layers.
---

# Project Architecture

## First principle

The existing project architecture is the source of truth.

Before introducing a new pattern:

1. Inspect similar existing functionality.
2. Follow established conventions.
3. Reuse existing abstractions.
4. Avoid unnecessary refactoring.

Do not introduce Clean Architecture, Hexagonal Architecture, CQRS, DDD or another architectural style unless the project already uses it or the user explicitly requests it.

## Layering

Follow the architecture already established by the project.

A typical Spring application may use:

Controller
↓
Service / Application layer
↓
Repository
↓
Database

Do not bypass layers without a clear reason.

## Business logic

Business rules should not live inside controllers.

Avoid putting significant business logic inside:

- Controllers.
- Repository implementations.
- DTOs.
- Configuration classes.

Business logic should live in the appropriate service or domain layer according to the existing project architecture.

## Dependencies

Before adding a dependency:

1. Check whether the functionality already exists.
2. Check existing dependencies.
3. Verify Java 17 compatibility.
4. Verify Spring Boot compatibility.
5. Consider maintenance and security implications.

Do not add a library for functionality that can reasonably be implemented with existing dependencies.

## Refactoring

Do not perform unrelated refactoring while implementing a feature.

Keep changes focused.

If architectural problems are discovered:

1. Complete the requested task safely.
2. Mention the architectural issue.
3. Suggest a separate refactoring if necessary.

## Database

Database changes must follow the project's migration system.

Never manually change production schema through application startup unless that is explicitly how the project is designed.

## External systems

External API integrations should be isolated behind an appropriate abstraction.

Do not spread HTTP client implementation details throughout business logic.

## Observability

Follow existing logging and monitoring conventions.

Do not log:

- Passwords.
- Access tokens.
- API keys.
- Secrets.
- Sensitive personal information.

## Production code

Before considering a change complete:

- Compile the project.
- Run relevant tests.
- Check static analysis if configured.
- Check formatting if configured.
- Review changed files.
- Verify that no secrets or debug code were introduced.

## Minimal changes

Prefer the smallest change that correctly solves the requested problem.

Do not rewrite working code merely because another implementation is theoretically cleaner.
