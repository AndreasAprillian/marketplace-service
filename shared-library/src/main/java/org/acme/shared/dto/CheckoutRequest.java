package org.acme.shared.dto;

import lombok.Builder;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Builder
public class CheckoutRequest {

    @Schema(hidden = true)
    private String orderId;

    private List<CartItem> items;

    private String paymentMethod;

    @Schema(hidden = true)
    private String email;

    @Schema(hidden = true)
    private String customerUsername;
}
