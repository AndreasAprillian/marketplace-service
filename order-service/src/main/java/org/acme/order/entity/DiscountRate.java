package org.acme.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "discount_rates")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRate extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "min_total", precision = 16, scale = 2)
    public BigDecimal minTotal;

    @Column(name = "discount_percent")
    public int discountPercent;
}
