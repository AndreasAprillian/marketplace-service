package org.acme.order.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.acme.shared.constant.KafkaTopic;
import org.acme.shared.dto.CheckoutRequest;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class OrderConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Named("checkout_workflow")
    Process<? extends Model> checkoutProcess;

    @Incoming(KafkaTopic.ORDER_CREATED)
    @Transactional
    public void consumeOrderCreated(String message) throws Exception {
        CheckoutRequest event = objectMapper.readValue(message, CheckoutRequest.class);

        Map<String, Object> variables = new HashMap<>();
        variables.put("checkout", event);
        variables.put("orderId", event.getOrderId());
        variables.put("region", event.getRegion());

        Model model = checkoutProcess.createModel();
        model.fromMap(variables);

        ProcessInstance<?> instance = checkoutProcess.createInstance(model);
        instance.start();
    }
}
