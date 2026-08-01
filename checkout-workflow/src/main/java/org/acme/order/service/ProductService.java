package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.dto.CheckoutRequest;

@ApplicationScoped
public class ProductService {

    public boolean validateProduct(CheckoutRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return false;
        }
        for (var item : request.getItems()) {
            if (!InMemoryStore.PRODUCTS.containsKey(item.getProductId())) {
                return false;
            }
        }
        return true;
    }
}
