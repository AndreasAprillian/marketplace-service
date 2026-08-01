# marketplace-service

Marketplace Quarkus multi-modul dengan service-service independen, masing-masing berjalan di port sendiri dan berbagi satu database PostgreSQL (`marketplace-db`) dengan skema yang sama, serta Kafka untuk messaging berbasis event.

## Modul

| Modul | Tipe | Port | Deskripsi                                                     |
| ------ | ---- | ---- |----------------------------------------------------------------|
| `shared-library` | library (jar) | - | Kode (DTO, base entity, konstanta) yang dipakai semua service |
| `customer-service` | application | 8081 | Create dan Read Customer                                       |
| `order-service` | application | 8082 | Create dan Read Order                                                     |
| `checkout-service` | application | 8083 | Checkout (tanpa DB, hanya Kafka + REST)                            |
| `email-service` | application | 8084 | Email mengonsumsi event                                      |
| `checkout-workflow` | application | 8085 | Model BPMN alur order via Kogito, hanya service in-memory, tanpa DB/Kafka |

## Infrastruktur (Docker)

PostgreSQL dan Kafka berjalan di Docker melalui `docker/docker-compose.yml`:

```shell script
docker compose -f docker/docker-compose.yml up -d
```

| Service | Host port | Detail |
| ------- | --------- | ------- |
| PostgreSQL | 5433 | db `marketplace-db`, user/pass `postgres/postgres`, schema `public` |
| Kafka | 9092 | topic dibuat otomatis (`marketplace.*.events`) |
| Kafka UI | 8089 | http://localhost:8089 |

Skema dan data seed dibuat otomatis saat pertama kali dari `docker/postgres/init/01-init.sql`.

Semua service yang memakai database terhubung ke PostgreSQL yang sama:

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5433/marketplace-db
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres

kafka.bootstrap.servers=localhost:9092
```

## Building

Build seluruh proyek (semua modul):

```shell script
./mvnw package
```

## Menjalankan dev mode

Setiap modul aplikasi dijalankan satu per satu dengan `-pl`:

```shell script
./mvnw quarkus:dev -pl customer-service
./mvnw quarkus:dev -pl order-service
./mvnw quarkus:dev -pl checkout-service
./mvnw quarkus:dev -pl email-service
./mvnw quarkus:dev -pl checkout-workflow
```

OpenAPI / Swagger UI untuk setiap service: `http://localhost:<port>/q/swagger-ui`

### checkout-workflow (Kogito BPMN)

`checkout-workflow` memodelkan alur `OrderWorkflowService` order-service sebagai proses BPMN yang dieksekusi oleh Kogito (jBPM 10). Bean service-nya bersifat in-memory (tanpa DB/Kafka). Catatan:

- Proses dieksekusi lewat test Kogito atau endpoint auto-generated `POST /checkout-workflow` (payload `CheckoutRequest`).
- Test BPMN:

```shell script
./mvnw test -pl checkout-workflow -am
```

## Packaging dan menjalankan aplikasi

```shell script
./mvnw package
```

Setiap service menghasilkan `quarkus-run.jar` di direktori `target/quarkus-app/` masing-masing, dijalankan dengan:

```shell script
java -jar <module>/target/quarkus-app/quarkus-run.jar
```

## Membuat native executable

```shell script
./mvnw package -Dnative
```

Atau, jika kamu tidak punya GraalVM, jalankan build native executable dalam container:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```
