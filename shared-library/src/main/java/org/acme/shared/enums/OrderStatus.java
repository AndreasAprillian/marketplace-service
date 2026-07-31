package org.acme.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderStatus {
    CREATED(1L,"Created"),
    VALIDATED(2L,"Validated"),
    PROCESSED(3L,"Processed"),
    FAILED(4L,"Failed");

    private Long id;
    private String name;
}
