---
name: spring-testing
description: >
  Use when creating or modifying unit tests, integration tests,
  Spring Boot tests, repository tests, controller tests or Testcontainers.
---

# Spring Testing

## General

Every meaningful code change should have appropriate tests.

Before writing tests:

1. Inspect existing test conventions.
2. Determine whether the test should be unit or integration.
3. Reuse existing fixtures and utilities.
4. Avoid introducing a different testing style without a reason.

## Unit tests

Use JUnit 5.

Use Mockito when isolation from dependencies is useful.

Do not mock everything automatically.

Test behaviour rather than implementation details.

## Service tests

Service tests should verify business rules and important edge cases.

Cover:

- Valid input.
- Invalid input.
- Expected business exceptions.
- Dependency failures when relevant.
- Boundary conditions.

## Controller tests

Use `MockMvc` or the project's established HTTP testing approach.

Test:

- HTTP status.
- Response body.
- Request validation.
- Error handling.
- Security behaviour when relevant.

## Repository tests

Use appropriate Spring Data testing support.

Verify:

- Queries.
- Relationships.
- Constraints.
- Persistence behaviour.
- Pagination when applicable.

## Integration tests

Use `@SpringBootTest` when the full Spring application context is required.

Do not use `@SpringBootTest` for every unit test.

Choose the smallest test scope that provides meaningful coverage.

## Testcontainers

Use Testcontainers when testing against real infrastructure is important.

Examples:

- PostgreSQL.
- Redis.
- Kafka.
- Elasticsearch.

Prefer real infrastructure over excessive mocking for infrastructure integration tests.

## Test quality

Tests should:

- Be deterministic.
- Be independent.
- Have clear names.
- Test one behaviour at a time.
- Avoid unnecessary implementation coupling.

Never remove a failing test simply because it exposes a bug.

Fix the implementation or update the test only when the expected behaviour has intentionally changed.

## Test names

Test names should clearly describe the expected behaviour.

Prefer names such as:

`shouldCreateOrderWhenRequestIsValid`

over:

`testCreateOrder`

## Before finishing

Run the relevant tests.

If possible, run the complete test suite before considering the change complete.
