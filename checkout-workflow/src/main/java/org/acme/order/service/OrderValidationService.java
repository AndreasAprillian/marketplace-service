package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderValidationService {

    public boolean isOrderExists(String orderId) {
        return InMemoryStore.EXISTING_ORDER_IDS.contains(orderId);
    }
}
