package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.producer.OrderFailedProducer;
import org.acme.shared.dto.CheckoutRequest;
import org.acme.shared.dto.PaymentEvent;

@ApplicationScoped
public class OrderFailedService {

    @Inject
    OrderFailedProducer orderFailedProducer;

    public void orderProcesFailed(CheckoutRequest request){
        PaymentEvent failEvent = PaymentEvent.builder()
                .orderId(request.getOrderId())
                .status("FAILED")
                .customerUsername(request.getCustomerUsername())
                .email(request.getEmail())
                .build();

        System.out.println("Order Gagal !");
        orderFailedProducer.sendOrderFailed(failEvent);
    }
}
