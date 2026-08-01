package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.dto.CheckoutRequest;

import java.math.BigDecimal;

@ApplicationScoped
public class SubTotalCalculationService {

    public BigDecimal calculateSubtotal(CheckoutRequest request) {
        return request.getItems().stream()
                .map(item -> {
                    InMemoryStore.Product product = InMemoryStore.PRODUCTS.get(item.getProductId());
                    return product != null
                            ? product.price.multiply(BigDecimal.valueOf(item.getQuantity()))
                            : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
