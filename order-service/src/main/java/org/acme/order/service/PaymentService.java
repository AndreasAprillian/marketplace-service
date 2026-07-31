package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.producer.PaymentProducer;
import org.acme.shared.dto.PaymentEvent;

import java.math.BigDecimal;

@ApplicationScoped
public class PaymentService {

    @Inject
    PaymentProducer paymentProducer;

    public void processPayment(String orderId, BigDecimal amount, String paymentMethod,
                               String customerUsername, String email) {
        PaymentEvent event = PaymentEvent.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status("SUCCESS")
                .customerUsername(customerUsername)
                .email(email)
                .build();
        paymentProducer.sendPaymentEvent(event);
    }
}
