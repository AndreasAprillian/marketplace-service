package org.acme.order.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.order.entity.OrderItem;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepository<OrderItem> {

    public java.util.List<OrderItem> findByOrderId(String orderId) {
        return list("orderId", orderId);
    }
}
