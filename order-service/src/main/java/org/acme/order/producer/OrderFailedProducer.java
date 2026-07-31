package org.acme.order.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.constant.KafkaTopic;
import org.acme.shared.dto.PaymentEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderFailedProducer {

    @Channel(KafkaTopic.ORDER_FAILED)
    Emitter<String> failedEmitter;

    public void sendOrderFailed(PaymentEvent event) {
        try {
            String json = new ObjectMapper().writeValueAsString(event);
            failedEmitter.send(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send order failed event", e);
        }
    }
}
