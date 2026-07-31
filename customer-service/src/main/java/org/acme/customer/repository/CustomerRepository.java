package org.acme.customer.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.customer.entity.CustomerEntity;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<CustomerEntity> {

    public CustomerEntity findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }
}
