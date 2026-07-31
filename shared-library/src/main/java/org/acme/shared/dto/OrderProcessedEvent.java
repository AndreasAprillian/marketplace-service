package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderProcessedEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;
    private BigDecimal discount;
    private String email;
    private Long customerId;
    private Long status;
    private Long validationResult;
}
