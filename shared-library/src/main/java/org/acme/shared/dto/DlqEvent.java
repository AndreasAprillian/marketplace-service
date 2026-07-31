package org.acme.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DlqEvent {
    private Object payload;
    private String error;
}
