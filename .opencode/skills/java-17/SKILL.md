---
name: java-17
description: >
  Use when writing or modifying Java code in this project.
  The project uses Java 17. Enforce Java 17 language features,
  APIs and compatibility constraints.
---

# Java 17 Development

The project uses Java 17.

## Compatibility

- Write code compatible with Java 17.
- Do not use APIs or language features introduced after Java 17.
- Do not use Java 21+ features such as:
  - Virtual threads
  - Sequenced collections
  - Pattern matching for switch finalized in Java 21
  - Record patterns
  - String templates
- Use Java 17 standard APIs where possible.

## Code style

- Prefer clear, readable and maintainable code.
- Prefer immutable objects when practical.
- Use `final` when it improves clarity.
- Prefer constructor injection in Spring components.
- Avoid unnecessary abstractions.
- Avoid premature optimization.

## Modern Java 17

Use appropriate Java 17 features:

- Records for immutable DTOs and simple data carriers.
- Pattern matching for `instanceof` where it improves readability.
- Text blocks for multiline strings.
- Switch expressions where appropriate.
- Sealed classes only when they provide a clear architectural benefit.

## Null handling

- Avoid returning null when an empty collection or Optional is more appropriate.
- Do not use Optional as a field or method parameter unless there is a specific reason.
- Validate nullable external input explicitly.

## Exceptions

- Do not swallow exceptions.
- Preserve the original cause when wrapping exceptions.
- Use domain/application exceptions where appropriate.
- Do not use exceptions for normal control flow.

## Before changing code

1. Inspect existing code.
2. Follow the project's existing conventions.
3. Check the Java version in the build configuration.
4. Avoid introducing dependencies unnecessarily.
5. Keep changes focused.
