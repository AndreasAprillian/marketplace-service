# checkout-workflow

Modul ini menggambarkan **BPMN proses checkout** marketplace yang dieksekusi dengan **Kogito** di atas **Quarkus 3**. BPMN yang sama persis kini juga di-embed di `order-service` dan trigger dari event Kafka `ORDER_CREATED`; di modul ini alur tersebut bisa dijalankan di test tanpa membutuhkan database atau Kafka.

- Port aplikasi: `8085`
- Dependency Kogito: `org.jbpm:jbpm-quarkus` (`org.kie.kogito:kogito-bom:10.2.0`)

## Menjalankan test

```sh
./mvnw -pl checkout-workflow test
```

`CheckoutWorkflowProcessTest` memverifikasi empat jalur:
1. **Stok tersedia** (`P001` qty 1) → proses selesai (`STATE_COMPLETED`), subtotal/ongkir/diskon/total terhitung benar, order tercatat, event pembayaran `SUCCESS`.
2. **Stok tidak cukup** (`P002` qty 99 > stok 5) → jalur gagal, event `FAILED`, order tidak dibuat.
3. **Produk tidak dikenal** (`P999`) → validasi produk gagal, event `FAILED`, order tidak dibuat.
4. **Order duplikat** (`ORD-99999` sudah ada) → pembuatan order dilewati, pembayaran tetap `SUCCESS`.

## Endpoint auto-generated

Kogito otomatis membuat endpoint REST dari BPMN, misalnya:

```
POST /checkout-workflow
GET  /checkout-workflow
```

