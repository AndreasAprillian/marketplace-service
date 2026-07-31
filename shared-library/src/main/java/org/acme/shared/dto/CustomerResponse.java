package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNo;
    private String region;
}
