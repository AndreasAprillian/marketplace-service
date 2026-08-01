package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.dto.CheckoutRequest;

import java.math.BigDecimal;

@ApplicationScoped
public class OrderCreationService {

    public void createOrder(CheckoutRequest request, BigDecimal subtotal, BigDecimal shippingCost,
                            BigDecimal discount, BigDecimal total) {
        InMemoryStore.ORDER_IDS_CREATED.add(request.getOrderId());
        System.out.println("Order " + request.getOrderId() + " created for customer: "
                + request.getCustomerUsername() + " | subtotal=" + subtotal
                + " shipping=" + shippingCost + " discount=" + discount + " total=" + total);
    }
}
