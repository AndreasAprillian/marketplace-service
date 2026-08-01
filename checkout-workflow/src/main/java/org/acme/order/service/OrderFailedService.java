package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.shared.dto.CheckoutRequest;
import org.acme.shared.dto.PaymentEvent;

@ApplicationScoped
public class OrderFailedService {

    @Inject
    PaymentEventLog paymentEventLog;

    public void orderProcesFailed(CheckoutRequest request) {
        PaymentEvent event = PaymentEvent.builder()
                .orderId(request.getOrderId())
                .status("FAILED")
                .customerUsername(request.getCustomerUsername())
                .email(request.getEmail())
                .build();
        paymentEventLog.record(event);
        System.out.println("Order FAILED published for order " + request.getOrderId());
    }
}
