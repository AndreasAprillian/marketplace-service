package org.acme.checkout.producer;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.constant.KafkaTopic;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderProducer {

    @Channel(KafkaTopic.ORDER_CREATED)
    Emitter<String> orderEmitter;

    public void sendOrder(String orderJson) {
        orderEmitter.send(orderJson);
    }
}
