package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.entity.ShippingRate;
import org.acme.order.repository.ShippingRateRepository;

import java.math.BigDecimal;

@ApplicationScoped
public class ShippingService {

    @Inject
    ShippingRateRepository shippingRateRepository;

    public BigDecimal calculateShipping(String region) {
        ShippingRate rate = shippingRateRepository.find("region", region).firstResult();
        return rate != null ? rate.rate : BigDecimal.TEN;
    }
}
