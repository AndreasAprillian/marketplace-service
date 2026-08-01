package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.shared.dto.CheckoutRequest;

@ApplicationScoped
public class CartValidationService {

    @Inject
    OrderFailedService orderFailedService;

    public void validateCart(CheckoutRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            orderFailedService.orderProcesFailed(request);
            return;
        }
        for (var item : request.getItems()) {
            if (!InMemoryStore.PRODUCTS.containsKey(item.getProductId())) {
                orderFailedService.orderProcesFailed(request);
            }
        }
    }
}
