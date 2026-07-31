package org.acme.order.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.workflow.OrderWorkflowService;
import org.acme.shared.constant.KafkaTopic;
import org.acme.shared.dto.CheckoutRequest;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class OrderConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    OrderWorkflowService workflowService;

    @Incoming(KafkaTopic.ORDER_CREATED)
    public void consumeOrderCreated(String message) throws Exception {
        CheckoutRequest event = objectMapper.readValue(message, CheckoutRequest.class);
        workflowService.processOrder(event);
    }
}
