---
name: spring-web
description: >
  Use when creating or modifying REST APIs, controllers, HTTP endpoints,
  request validation, DTOs and HTTP error responses in Spring Boot.
---

# Spring Web / REST

## Controllers

Controllers should be thin.

They should:

- Receive HTTP requests.
- Validate input.
- Delegate business operations.
- Convert application results into HTTP responses.

They should not contain complex business logic.

## DTOs

Do not expose JPA entities directly through REST APIs.

Prefer request and response DTOs.

Use Java records for simple immutable DTOs when appropriate.

## HTTP methods

Use HTTP semantics correctly:

- GET for retrieval.
- POST for creation.
- PUT for complete replacement.
- PATCH for partial updates.
- DELETE for deletion.

## Status codes

Use appropriate status codes:

- 200 OK
- 201 CREATED
- 204 NO CONTENT
- 400 BAD REQUEST
- 401 UNAUTHORIZED
- 403 FORBIDDEN
- 404 NOT FOUND
- 409 CONFLICT
- 422 UNPROCESSABLE ENTITY when appropriate
- 500 only for unexpected server errors

Do not return 200 for every situation.

## Validation

Use Jakarta Bean Validation.

Example:

```java
@PostMapping
public ResponseEntity<UserResponse> create(
        @Valid @RequestBody CreateUserRequest request) {
    ...
}
