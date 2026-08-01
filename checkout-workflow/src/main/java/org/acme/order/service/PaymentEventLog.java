package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.shared.dto.PaymentEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class PaymentEventLog {

    private final List<PaymentEvent> events = new CopyOnWriteArrayList<>();

    public void record(PaymentEvent event) {
        events.add(event);
    }

    public List<PaymentEvent> getEvents() {
        return events;
    }

    public void clear() {
        events.clear();
    }
}
