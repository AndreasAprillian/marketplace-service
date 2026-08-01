package org.acme.email.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.email.service.EmailService;
import org.acme.shared.constant.KafkaTopic;
import org.acme.shared.dto.PaymentEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EmailPaymentConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    EmailService emailService;

    @Incoming(KafkaTopic.PAYMENT_PROCESSED)
    public void consumePaymentProcessed(String message) throws Exception {
        PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
        emailService.sendEmail(event.getOrderId(), event.getEmail(), "Waiting for Payment",
                "Your payment of " + event.getAmount() + " via " + event.getPaymentMethod()
                        + " for order " + event.getOrderId() + " is waiting for payment.");
    }

    @Incoming(KafkaTopic.ORDER_FAILED)
    public void consumeOrderFailed(String message) throws Exception {
        PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
        emailService.sendEmail(event.getOrderId(), event.getEmail(), "Order Failed",
                "Your order " + event.getOrderId() + " has failed due to insufficient stock.");
    }
}
