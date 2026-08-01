package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.producer.PaymentProducer;
import org.acme.shared.dto.CheckoutRequest;
import org.acme.shared.dto.PaymentEvent;

import java.math.BigDecimal;

@ApplicationScoped
public class PaymentService {

    @Inject
    PaymentProducer paymentProducer;

    public void processPayment(CheckoutRequest request, BigDecimal total) {
        PaymentEvent event = PaymentEvent.builder()
                .orderId(request.getOrderId())
                .amount(total)
                .paymentMethod(request.getPaymentMethod())
                .status("SUCCESS")
                .customerUsername(request.getCustomerUsername())
                .email(request.getEmail())
                .build();
        paymentProducer.sendPaymentEvent(event);
    }
}
