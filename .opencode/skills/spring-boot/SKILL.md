---
name: spring-boot
description: >
  Use when developing, modifying or reviewing Spring Boot applications.
  Covers application structure, dependency injection, configuration,
  services, controllers, exception handling and Spring Boot conventions.
---

# Spring Boot Development

The project uses Java 17 and Spring Boot.

## First rule

Before modifying code:

1. Inspect the project structure.
2. Inspect `pom.xml` or `build.gradle`.
3. Determine the exact Spring Boot version.
4. Inspect existing architecture and conventions.
5. Reuse existing patterns.
6. Do not introduce a new architecture without a reason.

## Dependency injection

Prefer constructor injection.

Good:

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
