package org.acme.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ValidationStatus {

    VALID(1L,"Valid"),
    INVALID(2L,"Invalid");

    private Long id;
    private String name;
}
