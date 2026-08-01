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

Kedua soal saling terhubung: event Kafka `ORDER_CREATED` dari `checkout-service` memicu proses BPMN Kogito di dalam `order-service` (Soal 1 + Soal 2 terintegrasi). Modul `checkout-workflow` memakai BPMN yang sama persis sebagai ilustrasi mandiri tanpa DB/Kafka.

### Soal 1 — Service Kafka: terima → manipulasi → tulis balik/DB (dijawab oleh `order-service`)

Alur lengkap (dengan `checkout-service` sebagai producer dan `email-service` sebagai consumer):

```
Klien ──POST /checkout──▶ checkout-service (8083) ──(ORDER_CREATED)──▶ Kafka ──▶ order-service (8082)
                                                                          │ OrderConsumer (@Incoming ORDER_CREATED)
                                                                          │   └▶ start instance BPMN "checkout_workflow"
                                                                          │      (Validate Product → Check Stock → hitung
                                                                          │       subtotal/ongkir/diskon/total → Create Order → Payment)
                                                                          ├──(PAYMENT_PROCESSED)──▶ Kafka ──▶ email-service (8084)
                                                                          └──(ORDER_FAILED)───────▶ Kafka ──▶ email-service (8084)
```

- **Terima dari Kafka**: `order-service/src/main/java/org/acme/order/consumer/OrderConsumer.java` (`@Incoming(ORDER_CREATED)` + `@Transactional`), lalu menstart instance proses Kogito `checkout_workflow` — qualifier bean `@Named("checkout_workflow")` (underscore, sesuai id proses di BPMN).
- **Manipulasi data**: dieksekusi oleh BPMN `order-service/src/main/resources/checkout-workflow.bpmn2` lewat Service Task: `ProductService.validateProduct` → `StockService.checkStock` (validasi + kurangi stok) → `SubTotalCalculationService` → `ShippingService` → `DiscountCalculationService` → `TotalCalculationService` → `OrderValidationService` → `OrderCreationService`.
- **Tulis balik ke Kafka**: `order-service/.../producer/PaymentProducer.java` (`PAYMENT_PROCESSED`) dan `OrderFailedProducer.java` (`ORDER_FAILED`), dipanggil dari Service Task `PaymentService.processPayment` dan `OrderFailedService.orderProcesFailed`.
- **Simpan ke database**: `order-service/.../service/OrderCreationService.java` (persist `OrderEntity` + `OrderItem` ke PostgreSQL via Hibernate Panache).
- **Tech stack**: Java 17 (`maven.compiler.release=17`), Quarkus 3.31 + Kogito `jbpm-quarkus` (10.2.0) di `order-service`, PostgreSQL + Kafka (Docker).

### Soal 2 — BPMN checkout dengan Kogito + ilustrasi di test (BPMN yang sama di-embed di `order-service`, diilustrasikan mandiri di `checkout-workflow`)

Proses BPMN dieksekusi langsung oleh Kogito (jBPM 10) baik di dalam `order-service` (dipicu event Kafka) maupun mandiri di `checkout-workflow` (bean in-memory):

```
Start → Validate Product → [Product Exist?]
   ├─(tidak)→ Publish Order Failed → End (Failed)
   └─(ya)→ Check Stock → [Stock Available?]
      ├─(tidak)→ Publish Order Failed → End (Failed)
      └─(ya)→ Calculate Subtotal → Calculate Shipping → Calculate Discount → Calculate Total
             → Order Validation → [Order Exists?]
                ├─(ya)→ Merge
                └─(tidak)→ Create Order → Merge
             → Process Payment → End (Done)
```

- **File BPMN**: `checkout-workflow/src/main/resources/checkout-workflow.bpmn2` (salinan identik: `order-service/src/main/resources/checkout-workflow.bpmn2`)
- **Penjelasan service yang dipakai**: `checkout-workflow/README.md` (tabel pemetaan Service Task → class/method, mis. `ProductService.validateProduct`, `StockService.checkStock`, `OrderValidationService.isOrderExists`, `PaymentService.processPayment`)
- **Ilustrasi BPMN di test code (Kogito)**: `checkout-workflow/src/test/java/org/acme/order/CheckoutWorkflowProcessTest.java` — 4 skenario (stok cukup → SUCCESS, stok kurang → ORDER_FAILED, produk tidak dikenal → ORDER_FAILED, order duplikat → lewati pembuatan order), inject `Process<? extends Model>` dengan `@Named("checkout_workflow")`, jalankan instance, lalu assert variabel hasil + `PaymentEventLog`

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

`checkout-workflow` memodelkan BPMN yang sama persis dengan yang dieksekusi `order-service` sebagai proses Kogito (jBPM 10), namun bean service-nya bersifat in-memory (tanpa DB/Kafka). Catatan:

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
