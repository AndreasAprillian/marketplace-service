package org.acme.notification.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "recipient")
    public String recipient;

    @Column(name = "subject")
    public String subject;

    @Column(name = "body")
    public String body;

    @Column(name = "status")
    public String status;

    @Column(name = "createdAt")
    public LocalDateTime createdAt;

    @Column(name = "order_id")
    public String orderId;
}
