package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.order.entity.Product;
import org.acme.order.repository.ProductRepository;
import org.acme.shared.dto.CartItem;
import org.acme.shared.dto.CheckoutRequest;

import java.util.List;

@ApplicationScoped
public class StockService {

    @Inject
    ProductRepository productRepository;

    @Transactional
    public boolean checkStock(CheckoutRequest request) {
        List<CartItem> items = request.getItems();

        for (var item : items) {
            Product product = productRepository.find("id", item.getProductId()).firstResult();
            if (product == null || product.stock < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }
}
