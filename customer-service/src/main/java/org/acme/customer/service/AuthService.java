package org.acme.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.customer.dto.LoginRequest;
import org.acme.customer.dto.LoginResponse;
import org.acme.customer.dto.RegisterRequest;
import org.acme.customer.entity.CustomerEntity;
import org.acme.customer.producer.CustomerEventProducer;
import org.acme.customer.repository.CustomerRepository;
import org.acme.customer.security.JwtTokenProvider;
import org.acme.shared.dto.CustomerRegisteredEvent;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AuthService {

    @Inject
    CustomerRepository customerRepository;

    @Inject
    JwtTokenProvider tokenProvider;

    @Inject
    CustomerEventProducer eventProducer;

    public LoginResponse login(LoginRequest request) {
        CustomerEntity customer = customerRepository.findByUsername(request.getUsername());
        if (customer == null || !BCrypt.checkpw(request.getPassword(), customer.password)) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = tokenProvider.generateToken(customer);
        return LoginResponse.builder().token(token).username(customer.username).build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        CustomerEntity customer = CustomerEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .build();
        customerRepository.persist(customer);

        publishCustomerRegistered(customer);
    }

    private void publishCustomerRegistered(CustomerEntity customer) {
        try {
            CustomerRegisteredEvent event = CustomerRegisteredEvent.builder()
                    .phoneNo(customer.getPhoneNo())
                    .customerId(customer.getId())
                    .username(customer.getUsername())
                    .email(customer.getEmail())
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            eventProducer.sendCustomerRegistered(mapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
