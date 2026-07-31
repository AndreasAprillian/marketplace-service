package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentEvent {
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String customerUsername;
    private String email;
    private String phoneNo;
}
