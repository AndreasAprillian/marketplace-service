package org.acme.email.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.email.service.EmailService;
import org.acme.shared.constant.KafkaTopic;
import org.acme.shared.dto.CustomerRegisteredEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EmailCustomerRegisteredConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    EmailService emailService;

    @Incoming(KafkaTopic.CUSTOMER_REGISTERED)
    public void consumeCustomerRegistered(String message) throws Exception {
        CustomerRegisteredEvent event = objectMapper.readValue(message, CustomerRegisteredEvent.class);
        emailService.sendEmail(event.getUsername(), event.getEmail(), "Welcome to Marketplace",
                "Hi " + event.getUsername() + ", your account has been created successfully!");
    }
}
