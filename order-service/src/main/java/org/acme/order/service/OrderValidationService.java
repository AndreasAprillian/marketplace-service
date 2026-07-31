package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.repository.OrderRepository;

@ApplicationScoped
public class OrderValidationService {

    @Inject
    OrderRepository orderRepository;

    public boolean isOrderExists(String orderId) {
        return orderRepository.count("orderId", orderId) > 0;
    }
}
