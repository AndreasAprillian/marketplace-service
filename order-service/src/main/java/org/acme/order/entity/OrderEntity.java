package org.acme.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends PanacheEntityBase {

    @Id
    @Column(name = "order_id", nullable = false)
    public String orderId;

    @Column(name = "total", precision = 16, scale = 2)
    public BigDecimal total;

    @Column(name = "shipping_cost", precision = 16, scale = 2)
    public BigDecimal shippingCost;

    @Column(name = "discount", precision = 16, scale = 2)
    public BigDecimal discount;

    @Column(name = "sub_total", precision = 16, scale = 2)
    public BigDecimal subTotal;

    @Column(name = "payment_method")
    public String paymentMethod;

    @Column(name = "payment_status")
    public String paymentStatus;

    @Column(name = "customer_username")
    public String customerUsername;

    @Column(name = "email")
    public String email;

    @Column(name = "status")
    public String status;
}
