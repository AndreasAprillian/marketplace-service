package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class ShippingService {

    public BigDecimal calculateShipping(String region) {
        return InMemoryStore.SHIPPING_RATES.getOrDefault(region, InMemoryStore.DEFAULT_SHIPPING);
    }
}
