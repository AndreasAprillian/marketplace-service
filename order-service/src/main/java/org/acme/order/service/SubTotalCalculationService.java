package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.order.entity.Product;
import org.acme.shared.dto.CheckoutRequest;

import java.math.BigDecimal;

@ApplicationScoped
public class SubTotalCalculationService {

    public BigDecimal calculateSubtotal(CheckoutRequest request) {
        return request.getItems().stream()
                .map(item -> {
                    Product product = Product.findById(item.getProductId());
                    return product != null
                            ? product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                            : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
