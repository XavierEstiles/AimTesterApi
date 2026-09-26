# AimTester API

API REST de AimTester, construida con Java 17 y Spring Boot 3.5.6. Para información sobre el cliente React y el flujo de desarrollo completo, consulta el [README principal](../README.md).

## Requisitos

- Java 17
- Maven 3.9 o posterior

## Ejecutar

Desde este directorio (`AimTesterApi`):

```powershell
mvn spring-boot:run
```

La URL base es `http://localhost:8080/aim-tester-api`.

## Endpoints

| Método | Ruta | Acceso | Descripción |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Público | Recibe `username` y `password`; devuelve `{ "token": "..." }` |
| `GET` | `/player/me` | JWT | Devuelve el nombre del usuario autenticado |
| `GET` | `/player/health` | JWT | Devuelve el estado y la hora del API |
| `GET` | `/swagger-ui.html` | Público | Swagger UI |
| `GET` | `/v3/api-docs` | Público | Especificación OpenAPI |

Las rutas protegidas esperan `Authorization: Bearer <token>`. Los tokens duran 15 minutos y una petición autenticada válida devuelve un JWT renovado en la cabecera `X-Auth-Token`.

## Usuario de desarrollo

La configuración actual carga este usuario en memoria:

- Usuario: `admin`
- Contraseña: `admin123`

No hay base de datos conectada ni registro persistente. Cambia las credenciales y el secreto JWT antes de desplegar en un entorno real.

## Pruebas y empaquetado

```powershell
mvn test
mvn clean package
```

Para ejecutar el JAR generado:

```powershell
java -jar target/aim-tester-api-0.0.1-SNAPSHOT.jar
```

## Configuración

En `src/main/resources/application.properties`:

```properties
server.port=8080
server.servlet.context-path=/aim-tester-api
jwt.expiration=900000
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```
