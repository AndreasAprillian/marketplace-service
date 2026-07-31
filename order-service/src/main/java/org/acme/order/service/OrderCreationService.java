package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.order.entity.OrderEntity;
import org.acme.order.entity.OrderItem;
import org.acme.order.entity.Product;
import org.acme.order.repository.ProductRepository;
import org.acme.shared.dto.CartItem;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class OrderCreationService {

    @Inject
    ProductRepository productRepository;

    @Transactional
    public OrderEntity createOrder(String orderId, List<CartItem> items, String customerUsername,
                                   String email, String paymentMethod,
                                   BigDecimal subtotal, BigDecimal shippingCost,
                                   BigDecimal discount, BigDecimal total) {
        OrderEntity order = OrderEntity.builder()
                .orderId(orderId)
                .total(total)
                .subTotal(subtotal)
                .shippingCost(shippingCost)
                .discount(discount)
                .paymentMethod(paymentMethod)
                .paymentStatus("PENDING")
                .customerUsername(customerUsername)
                .email(email)
                .status("CREATED")
                .build();
        OrderEntity.persist(order);

        for (var item : items) {
            Product product = productRepository.find("id", item.getProductId()).firstResult();
            OrderItem orderItem = OrderItem.builder()
                    .orderId(orderId)
                    .productId(item.getProductId())
                    .productName(product != null ? product.name : item.getProductId())
                    .quantity(item.getQuantity())
                    .price(product != null ? product.price : BigDecimal.ZERO)
                    .build();
            OrderItem.persist(orderItem);
        }

        return order;
    }
}
