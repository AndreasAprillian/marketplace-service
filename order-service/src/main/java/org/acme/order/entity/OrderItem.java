package org.acme.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "order_id")
    public String orderId;

    @Column(name = "product_id")
    public String productId;

    @Column(name = "product_name")
    public String productName;

    @Column(name = "quantity")
    public int quantity;

    @Column(name = "price", precision = 16, scale = 2)
    public BigDecimal price;
}
