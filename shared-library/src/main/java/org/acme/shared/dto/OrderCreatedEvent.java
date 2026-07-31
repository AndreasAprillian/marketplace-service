package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal price;
    private String email;
    private String customerUsername;
    private String status;
}
