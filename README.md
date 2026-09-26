# Aim Tester API

API REST creada con Spring Boot 3.5.6 y Java 17.

## Requisitos

- Java 17
- Maven 3.9+

Verifica la versión instalada:

```bash
java -version
mvn -v
```

## Ejecutar la aplicación

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

La API quedará disponible en:

- http://localhost:8080/v1/aim-tester-api

## Swagger / OpenAPI

La documentación Swagger queda publicada en:

- http://localhost:8080/v1/aim-tester-api/swagger-ui.html
- http://localhost:8080/v1/aim-tester-api/v3/api-docs

También puedes abrir la especificación JSON directamente:

```bash
curl http://localhost:8080/v1/aim-tester-api/v3/api-docs
```

## Endpoint de salud

```bash
curl http://localhost:8080/v1/aim-tester-api/api/health
```

Respuesta esperada:

```json
{
  "status": "UP",
  "timestamp": "2026-09-25T13:00:00Z"
}
```

## Compilar y empaquetar

```bash
mvn clean package
```

## Ejecutar el JAR generado

```bash
java -jar target/aim-tester-api-0.0.1-SNAPSHOT.jar
```

> Con el context path configurado, la aplicación queda bajo la base URL: http://localhost:8080/v1/aim-tester-api

## Configuración relevante

```properties
server.port=8080
server.servlet.context-path=/v1/aim-tester-api

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```
