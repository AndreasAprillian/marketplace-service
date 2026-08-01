package org.acme.order;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.order.service.InMemoryStore;
import org.acme.order.service.PaymentEventLog;
import org.acme.shared.dto.CartItem;
import org.acme.shared.dto.CheckoutRequest;
import org.acme.shared.dto.PaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class CheckoutWorkflowProcessTest {

    @Inject
    @Named("checkout-workflow")
    Process<? extends Model> checkoutProcess;

    @Inject
    PaymentEventLog paymentEventLog;

    @BeforeEach
    void setUp() {
        paymentEventLog.clear();
    }

    private Model newProcessInstanceModel(CheckoutRequest checkout) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("checkout", checkout);
        variables.put("orderId", checkout.getOrderId());
        variables.put("region", checkout.getRegion());
        Model model = checkoutProcess.createModel();
        model.fromMap(variables);
        return model;
    }

    private CheckoutRequest newCheckout(String orderId, String productId, int quantity) {
        return CheckoutRequest.builder()
                .orderId(orderId)
                .items(List.of(CartItem.builder().productId(productId).quantity(quantity).build()))
                .paymentMethod("BANK_TRANSFER")
                .email("budi@example.com")
                .customerUsername("budi")
                .region("Jakarta")
                .build();
    }

    @Test
    void checkoutWithAvailableStockCompletesSuccessfully() {
        ProcessInstance<?> instance = checkoutProcess.createInstance(newProcessInstanceModel(newCheckout("ORD-10001", "P001", 1)));
        instance.start();

        assertEquals(ProcessInstance.STATE_COMPLETED, instance.status());
        Model result = (Model) instance.variables();
        assertEquals(new BigDecimal("500000"), result.toMap().get("subtotal"));
        assertEquals(new BigDecimal("10000"), result.toMap().get("shippingCost"));
        assertEquals(new BigDecimal("50000"), result.toMap().get("discount"));
        assertEquals(new BigDecimal("460000"), result.toMap().get("total"));
        assertTrue(InMemoryStore.ORDER_IDS_CREATED.contains("ORD-10001"));
        assertEquals("SUCCESS", lastEventStatus());
    }

    @Test
    void checkoutWithInsufficientStockPublishesOrderFailed() {
        ProcessInstance<?> instance = checkoutProcess.createInstance(newProcessInstanceModel(newCheckout("ORD-10002", "P002", 99)));
        instance.start();

        assertEquals(ProcessInstance.STATE_COMPLETED, instance.status());
        assertFalse(InMemoryStore.ORDER_IDS_CREATED.contains("ORD-10002"));
        assertEquals("FAILED", lastEventStatus());
    }

    @Test
    void duplicateOrderSkipsCreationButStillProcessesPayment() {
        ProcessInstance<?> instance = checkoutProcess.createInstance(newProcessInstanceModel(newCheckout("ORD-99999", "P001", 1)));
        instance.start();

        assertEquals(ProcessInstance.STATE_COMPLETED, instance.status());
        assertEquals("SUCCESS", lastEventStatus());
    }

    private String lastEventStatus() {
        List<PaymentEvent> events = paymentEventLog.getEvents();
        assertFalse(events.isEmpty());
        return events.get(events.size() - 1).getStatus();
    }
}
