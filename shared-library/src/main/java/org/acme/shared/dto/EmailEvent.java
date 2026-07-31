package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailEvent {
    private String to;
    private String subject;
    private String body;
}
