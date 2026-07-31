# marketplace-service

Multi-module Quarkus marketplace with independent services, each running on its own port and sharing a single PostgreSQL database (`marketplace-db`) and schema, plus Kafka for event-driven messaging.

## Modules

| Module | Type | Port | Description |
| ------ | ---- | ---- | ----------- |
| `shared-library` | library (jar) | - | Shared code (DTOs, base entity, constants) used by every service |
| `customer-service` | application | 8081 | Customer CRUD |
| `order-service` | application | 8082 | Order CRUD |
| `checkout-service` | application | 8083 | Checkout (no DB, Kafka + REST only) |
| `notification-service` | application | 8084 | Notification CRUD, consumes events |

## Infrastructure (Docker)

PostgreSQL and Kafka run in Docker via `docker/docker-compose.yml`:

```shell script
docker compose -f docker/docker-compose.yml up -d
```

| Service | Host port | Details |
| ------- | --------- | ------- |
| PostgreSQL | 5433 | db `marketplace-db`, user/pass `postgres/postgres`, schema `public` |
| Kafka | 9092 | topics created automatically (`marketplace.*.events`) |
| Kafka UI | 8089 | http://localhost:8089 |

The schema and seed data are created automatically on first start from `docker/postgres/init/01-init.sql`.

All services that use a database connect to the same PostgreSQL:

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5433/marketplace-db
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres

kafka.bootstrap.servers=localhost:9092
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
