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

## Pemetaan Soal Interview

### Soal 1 — Service Kafka: terima → manipulasi → tulis balik/DB (dijawab oleh `order-service`)

Alur lengkap (dengan `checkout-service` sebagai producer dan `email-service` sebagai consumer):

```
checkout-service ──(ORDER_CREATED)──▶ Kafka ──▶ order-service
                                                 │  OrderConsumer (Incoming ORDER_CREATED)
                                                 │  OrderWorkflowService.processOrder():
                                                 │    validateCart → checkStock → hitung subtotal/shipping/
                                                 │    discount/total → createOrder (simpan DB) → processPayment
                                                 ├──(PAYMENT_PROCESSED)──▶ Kafka ──▶ email-service
                                                 └──(ORDER_FAILED)───────▶ Kafka ──▶ email-service
```

- **Terima dari Kafka**: `order-service/src/main/java/org/acme/order/consumer/OrderConsumer.java` (`@Incoming("ORDER_CREATED")`)
- **Manipulasi data**: `order-service/src/main/java/org/acme/order/workflow/OrderWorkflowService.java` (orchestrasi validasi, cek stok, perhitungan total)
- **Tulis balik ke Kafka**: `order-service/.../producer/PaymentProducer.java` (`PAYMENT_PROCESSED`) dan `OrderFailedProducer.java` (`ORDER_FAILED`)
- **Simpan ke database**: `order-service/.../service/OrderCreationService.java` (persist `OrderEntity` + `OrderItem` ke PostgreSQL via Hibernate Panache)
- **Tech stack**: Java 17 (`maven.compiler.release=17`), Quarkus 3.38, PostgreSQL + Kafka (Docker), dataset order marketplace

### Soal 2 — BPMN checkout dengan Kogito + ilustrasi di test (dijawab oleh `checkout-workflow`)

Proses BPMN memodelkan `OrderWorkflowService` dan dieksekusi langsung oleh Kogito (jBPM 10):

```
Start → Validate Cart → Check Stock → [Stock Available?]
   ├─(tidak)→ Publish Order Failed → End (Failed)
   └─(ya)→ Calculate Subtotal → Calculate Shipping → Calculate Discount → Calculate Total
          → Order Validation → [Order Exists?]
             ├─(ya)→ Merge
             └─(tidak)→ Create Order → Merge
          → Process Payment → End (Done)
```

- **File BPMN**: `checkout-workflow/src/main/resources/checkout-workflow.bpmn2`
- **Penjelasan service yang dipakai**: `checkout-workflow/README.md` (tabel pemetaan Service Task → class/method, mis. `StockService.checkStock`, `OrderValidationService.isOrderExists`, `PaymentService.processPayment`)
- **Ilustrasi BPMN di test code (Kogito)**: `checkout-workflow/src/test/java/org/acme/order/CheckoutWorkflowProcessTest.java` — 3 skenario (stok cukup → SUCCESS, stok kurang → ORDER_FAILED, order duplikat → lewati pembuatan order), inject `Process<? extends Model>`, jalankan instance, lalu assert variabel hasil + `PaymentEventLog`

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
