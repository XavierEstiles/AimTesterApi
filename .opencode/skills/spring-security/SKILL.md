---
name: spring-security
description: >
  Use when implementing authentication, authorization, security configuration,
  JWT, roles, permissions, CORS, CSRF or protected Spring Boot endpoints.
---

# Spring Security

## Security first

Never disable security just to make a test or endpoint work.

Do not:

- Hardcode credentials.
- Log passwords.
- Log tokens.
- Store secrets in source control.
- Disable authentication globally to solve a local problem.

## Existing configuration

Before changing security:

1. Inspect the existing `SecurityFilterChain`.
2. Determine the authentication mechanism.
3. Inspect roles and authorities.
4. Inspect JWT or session configuration.
5. Inspect existing security tests.

Do not replace the security architecture unnecessarily.

## Authentication

Authentication determines who the user is.

Authorization determines whether the authenticated user can perform an operation.

Always consider both.

## Passwords

Passwords must never be stored in plaintext.

Use a secure password encoder provided by Spring Security.

## JWT

If JWT is used:

- Validate the signature.
- Validate expiration.
- Validate issuer and audience when required.
- Never trust claims without validation.
- Never log the complete token.

## Authorization

Use the project's existing authorization model.

When appropriate, use method-level authorization such as:

`@PreAuthorize("hasRole('ADMIN')")`

Do not rely exclusively on frontend authorization.

Authorization must be enforced server-side.

## CORS

Do not use permissive CORS configuration in production without a reason.

Avoid allowing every origin when credentials are enabled.

## CSRF

Do not disable CSRF blindly.

Understand whether the application uses:

- Browser sessions.
- Stateless APIs.
- JWT.
- Cookies.

before changing CSRF configuration.

## Secrets

Never place secrets in:

- Java source code.
- `application.yml`.
- `application.properties`.
- Git repositories.

Use environment variables or the project's secret-management mechanism.

## Security tests

When changing security configuration, add or update tests covering:

- Unauthenticated access.
- Authenticated access.
- Forbidden access.
- Required roles or authorities.
- Invalid credentials or tokens.
