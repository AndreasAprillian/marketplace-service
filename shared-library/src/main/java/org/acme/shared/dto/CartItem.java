package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItem {
    private String productId;
    private int quantity;
}
