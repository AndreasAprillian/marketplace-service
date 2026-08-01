package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.dto.CartItem;
import org.acme.shared.dto.CheckoutRequest;

@ApplicationScoped
public class StockService {

    public boolean checkStock(CheckoutRequest request) {
        synchronized (InMemoryStore.PRODUCTS) {
            for (CartItem item : request.getItems()) {
                InMemoryStore.Product product = InMemoryStore.PRODUCTS.get(item.getProductId());
                if (product == null || product.stock < item.getQuantity()) {
                    return false;
                }
            }
            for (CartItem item : request.getItems()) {
                InMemoryStore.Product product = InMemoryStore.PRODUCTS.get(item.getProductId());
                product.stock -= item.getQuantity();
            }
            return true;
        }
    }
}
