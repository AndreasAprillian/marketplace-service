package org.acme.order.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.order.entity.DiscountRate;

@ApplicationScoped
public class DiscountRateRepository implements PanacheRepository<DiscountRate> {
}
