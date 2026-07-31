package org.acme.notification.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.notification.entity.EmailLog;

@ApplicationScoped
public class EmailLogRepository implements PanacheRepository<EmailLog> {
}
