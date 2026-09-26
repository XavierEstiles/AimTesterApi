---
name: spring-data-jpa
description: >
  Use when working with JPA, Hibernate, Spring Data repositories,
  entities, relationships, queries, transactions and database persistence.
---

# Spring Data JPA

## Entities

Before modifying an entity:

1. Inspect its relationships.
2. Inspect existing queries.
3. Check database migrations.
4. Check serialization behaviour.
5. Check existing tests.

Do not modify entities casually.

## Relationships

Be careful with:

- `@OneToMany`
- `@ManyToOne`
- `@OneToOne`
- `@ManyToMany`

Prefer explicit relationship ownership.

Avoid unnecessary bidirectional relationships.

## Fetching

Be aware of lazy and eager loading.

Prefer `FetchType.LAZY` unless eager loading is explicitly justified.

## N+1 queries

Always consider N+1 query problems.

When loading related data, consider:

- `JOIN FETCH`
- `@EntityGraph`
- Projections
- Explicit queries

Choose the appropriate approach based on the use case.

## Transactions

Use `@Transactional` at the service or application boundary where appropriate.

For read-only operations, consider:

`@Transactional(readOnly = true)`

Do not add transactions blindly to every method.

## Repositories

Prefer Spring Data repositories when they are sufficient.

Use custom queries when necessary.

Do not create custom repository implementations without a concrete need.

## Queries

Before writing a new query:

1. Check existing repository methods.
2. Check whether a derived query is sufficient.
3. Check whether an existing query can be reused.
4. Consider performance.
5. Consider pagination for potentially large result sets.

## DTO projections

For read-heavy use cases, consider projections instead of loading complete entities when appropriate.

## Pagination

For endpoints that can return large datasets, prefer `Pageable` or another established pagination strategy.

Do not load large tables completely into memory.

## Entity serialization

Do not expose JPA entities directly from REST controllers.

Use DTOs to avoid:

- Lazy loading problems.
- Circular references.
- Accidental data exposure.
- Coupling API contracts to persistence models.

## Database migrations

Never modify the database schema manually if the project uses Flyway or Liquibase.

Follow the existing migration strategy.

Before changing an entity, inspect the corresponding database migration history.
