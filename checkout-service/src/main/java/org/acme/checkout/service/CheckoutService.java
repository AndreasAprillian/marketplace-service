package org.acme.checkout.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.checkout.producer.OrderProducer;
import org.acme.shared.dto.CheckoutRequest;

@ApplicationScoped
public class CheckoutService {

    @Inject
    OrderProducer orderProducer;

    public void processCheckout(CheckoutRequest request) {
        try {
            String orderJson = new ObjectMapper().writeValueAsString(request);
            orderProducer.sendOrder(orderJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process checkout", e);
        }
    }
}
