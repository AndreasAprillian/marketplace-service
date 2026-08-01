package org.acme.order.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryStore {

    public static final class Product {
        public final BigDecimal price;
        public int stock;

        public Product(BigDecimal price, int stock) {
            this.price = price;
            this.stock = stock;
        }
    }

    public static final Map<String, Product> PRODUCTS = new HashMap<>();
    public static final Map<String, BigDecimal> SHIPPING_RATES = new HashMap<>();
    public static final BigDecimal DEFAULT_SHIPPING = new BigDecimal("10000");
    public static final BigDecimal DISCOUNT_MIN_TOTAL = new BigDecimal("100000");
    public static final BigDecimal DISCOUNT_PERCENT = new BigDecimal("10");
    public static final Set<String> EXISTING_ORDER_IDS = Set.of("ORD-99999");
    public static final Set<String> ORDER_IDS_CREATED = ConcurrentHashMap.newKeySet();

    static {
        PRODUCTS.put("P001", new Product(new BigDecimal("500000"), 10));
        PRODUCTS.put("P002", new Product(new BigDecimal("100000"), 5));
        SHIPPING_RATES.put("Jakarta", new BigDecimal("10000"));
        SHIPPING_RATES.put("Bandung", new BigDecimal("15000"));
    }

    private InMemoryStore() {
    }
}
