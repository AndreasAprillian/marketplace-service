package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class DiscountCalculationService {

    public BigDecimal calculateDiscount(BigDecimal total) {
        if (total.compareTo(InMemoryStore.DISCOUNT_MIN_TOTAL) >= 0) {
            return total.multiply(InMemoryStore.DISCOUNT_PERCENT).divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}
