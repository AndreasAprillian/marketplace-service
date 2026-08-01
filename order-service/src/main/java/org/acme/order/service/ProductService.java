package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.entity.Product;
import org.acme.order.repository.ProductRepository;
import org.acme.shared.dto.CheckoutRequest;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    public boolean validateProduct(CheckoutRequest request) {
        for (var item : request.getItems()) {
            Product product = productRepository.find("id", item.getProductId()).firstResult();
            if (product == null) {
                return false;
            }
        }
        return true;
    }
}
