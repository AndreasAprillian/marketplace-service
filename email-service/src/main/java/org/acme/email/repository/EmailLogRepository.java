package org.acme.email.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.email.entity.EmailLog;

@ApplicationScoped
public class EmailLogRepository implements PanacheRepository<EmailLog> {
}
