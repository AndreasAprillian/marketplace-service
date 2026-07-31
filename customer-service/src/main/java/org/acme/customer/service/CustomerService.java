package org.acme.customer.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.customer.entity.CustomerEntity;
import org.acme.customer.repository.CustomerRepository;
import org.acme.shared.dto.CustomerResponse;

@ApplicationScoped
public class CustomerService {

    @Inject
    CustomerRepository customerRepository;

    public CustomerResponse getCustomer(String username) {
        CustomerEntity customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        return CustomerResponse.builder()
                .id(customer.id)
                .username(customer.username)
                .email(customer.email)
                .phoneNo(customer.phoneNo)
                .build();
    }
}
