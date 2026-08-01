package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class TotalCalculationService {

    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal shippingCost, BigDecimal discount) {
        return subtotal.add(shippingCost).subtract(discount);
    }
}
