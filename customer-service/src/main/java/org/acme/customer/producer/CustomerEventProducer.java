package org.acme.customer.producer;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.constant.KafkaTopic;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class CustomerEventProducer {

    @Channel(KafkaTopic.CUSTOMER_REGISTERED)
    Emitter<String> customerRegisteredEmitter;

    public void sendCustomerRegistered(String eventJson) {
        customerRegisteredEmitter.send(eventJson);
    }
}
