# marketplace-service

Multi-module Quarkus marketplace with independent services, each running on its own port and sharing a single PostgreSQL database (`marketplace`) and schema.

## Modules

| Module | Type | Port | Description |
| ------ | ---- | ---- | ----------- |
| `shared-library` | library (jar) | - | Shared code (DTOs, base entity, constants) used by every service |
| `customer-service` | application | 8081 | Customer CRUD |
| `order-service` | application | 8082 | Order CRUD |
| `checkout-service` | application | 8083 | Checkout CRUD |
| `notification-service` | application | 8084 | Notification CRUD |

## Database

All services connect to one PostgreSQL database:

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/marketplace
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
```

Create the database before running (single schema, e.g. `public`):

```shell script
psql -U postgres -c "CREATE DATABASE marketplace;"
```

## Building

Build the whole project (all modules):

```shell script
./mvnw package
```

## Running in dev mode

Each application module is started individually with `-pl`:

```shell script
./mvnw quarkus:dev -pl customer-service
./mvnw quarkus:dev -pl order-service
./mvnw quarkus:dev -pl checkout-service
./mvnw quarkus:dev -pl notification-service
```

OpenAPI / Swagger UI for each service: `http://localhost:<port>/q/swagger-ui`

## Packaging and running the application

```shell script
./mvnw package
```

Each service produces a `quarkus-run.jar` in its own `target/quarkus-app/` directory, runnable with:

```shell script
java -jar <module>/target/quarkus-app/quarkus-run.jar
```

## Creating a native executable

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, run the native executable build in a container:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```
