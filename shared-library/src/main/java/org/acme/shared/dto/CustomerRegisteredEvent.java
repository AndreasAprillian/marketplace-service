package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRegisteredEvent {
    private Long customerId;
    private String username;
    private String email;
    private String phoneNo;
}
