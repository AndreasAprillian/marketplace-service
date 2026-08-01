# checkout-workflow

Modul ini menggambarkan **BPMN proses checkout** marketplace yang dieksekusi dengan **Kogito** di atas **Quarkus 3**. Alur diagram meniru `OrderWorkflowService.processOrder()` pada `order-service`, namun dieksekusi sebagai proses BPMN yang bisa dijalankan di test tanpa membutuhkan database atau Kafka.

- Port aplikasi: `8085`
- Dependency Kogito: `org.jbpm:jbpm-quarkus` (`org.kie.kogito:kogito-bom:10.2.0`)

## BPMN: `src/main/resources/checkout-workflow.bpmn2`

Proses `checkout-workflow` menerima satu `CheckoutRequest` lalu berjalan mengikuti langkah-langkah yang sama persis dengan `OrderWorkflowService.processOrder`:

```
Start
  -> Validate Cart              (CartValidationService.validateCart)
  -> Check Stock                (StockService.checkStock -> variabel stockAvailable)
  -> Gateway "Stock Available?"
        |-- Available  (stockAvailable == true)
        |       -> Calculate Subtotal   (SubTotalCalculationService.calculateSubtotal)
        |       -> Calculate Shipping   (ShippingService.calculateShipping(region))
        |       -> Calculate Discount   (DiscountCalculationService.calculateDiscount)
        |       -> Calculate Total      (TotalCalculationService.calculateTotal)
        |       -> Gateway "Order Exists?"
        |             |-- Already exists (orderExists == true)  -> (lewati pembuatan order)
        |             |-- New order      -> Create Order        (OrderCreationService.createOrder)
        |             -> Merge
        |       -> Process Payment       (PaymentService.processPayment)
        |       -> End (Done)
        |
        \-- Not available -> Publish Order Failed (OrderFailedService.orderProcesFailed)
                         -> End (Failed)
```

Dua *exclusive gateway* memodelkan percabangan yang ada di kode:
- `Stock Available?` — bila `stockAvailable == false`, proses langsung publish **Order Failed** dan selesai.
- `Order Exists?` — bila `orderId` sudah pernah dibuat, pembuatan order dilewati (anti-duplikat), pembayaran tetap diproses.

## Service yang digunakan (sesuai `order-service`)

| BPMN Service Task | Class (order-service) | Metode | Peran di alur nyata                                                            |
| --- | --- | --- |--------------------------------------------------------------------------------|
| Validate Cart | `CartValidationService` | `validateCart(CheckoutRequest)` | Memastikan tiap `productId` di keranjang valid; jika tidak, triger `orderProcesFailed` |
| Check Stock | `StockService` | `checkStock(...)` | Mengecek stok tiap item dan **mengurangi stok**; mengembalikan `boolean`       |
| Publish Order Failed | `OrderFailedService` | `orderProcesFailed(CheckoutRequest)` | Menerbitkan event `ORDER_FAILED` (diganti catatan log)                         |
| Calculate Subtotal | `SubTotalCalculationService` | `calculateSubtotal(CheckoutRequest)` | Menjumlahkan `harga x qty` semua item                                          |
| Calculate Shipping | `ShippingService` | `calculateShipping(region)` | Mengambil tarif ongkir per region (`shipping_rates`), default `10000`          |
| Calculate Discount | `DiscountCalculationService` | `calculateDiscount(subtotal)` | Diskon persen dari `discount_rates` bila subtotal memenuhi `minTotal`          |
| Calculate Total | `TotalCalculationService` | `calculateTotal(subtotal, shippingCost, discount)` | `subtotal + shippingCost - discount`                                           |
| Create Order | `OrderCreationService` | `createOrder(...)` | Menyimpan order (status `CREATED`, payment `PENDING`) + order_items            |
| Process Payment | `PaymentService` | `processPayment(...)` | Menerbitkan event `PAYMENT_PROCESSED` (status `SUCCESS`)                       |

> Catatan: untuk bisa dieksekusi di test tanpa DB/Kafka, bean di modul ini (`org.acme.order.service.*`) adalah replika sederhana dari service `order-service` — nama class & metode sama, namun data produk/stok/ongkir/diskon disimpan in-memory di `InMemoryStore`, dan event Kafka diganti pencatatan ke `PaymentEventLog`.

## Menjalankan test

```sh
./mvnw -pl checkout-workflow test
```

`CheckoutWorkflowProcessTest` memverifikasi tiga jalur:
1. **Stok tersedia** (`P001` qty 1) → proses selesai (`STATE_COMPLETED`), subtotal/ongkir/diskon/total terhitung benar, order tercatat, event pembayaran `SUCCESS`.
2. **Stok tidak cukup** (`P002` qty 99 > stok 5) → jalur gagal, event `FAILED`, order tidak dibuat.
3. **Order duplikat** (`ORD-99999` sudah ada) → pembuatan order dilewati, pembayaran tetap `SUCCESS`.

## Endpoint auto-generated

Kogito otomatis membuat endpoint REST dari BPMN, misalnya:

```
POST /checkout-workflow
GET  /checkout-workflow
```

Contoh payload `POST /checkout-workflow`:

```json
{
  "checkout": {
    "orderId": "ORD-20001",
    "items": [{ "productId": "P001", "quantity": 1 }],
    "paymentMethod": "BANK_TRANSFER",
    "email": "budi@example.com",
    "customerUsername": "budi",
    "region": "Jakarta"
  },
  "region": "Jakarta",
  "orderId": "ORD-20001"
}
```
