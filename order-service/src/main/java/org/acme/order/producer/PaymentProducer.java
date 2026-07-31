package org.acme.order.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.constant.KafkaTopic;
import org.acme.shared.dto.PaymentEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class PaymentProducer {

    @Channel(KafkaTopic.PAYMENT_PROCESSED)
    Emitter<String> paymentEmitter;

    public void sendPaymentEvent(PaymentEvent event) {
        try {
            String json = new ObjectMapper().writeValueAsString(event);
            paymentEmitter.send(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send payment event", e);
        }
    }
}
