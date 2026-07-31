package org.acme.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_rates")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRate extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "region")
    public String region;

    @Column(name = "rate", precision = 16, scale = 2)
    public BigDecimal rate;
}
