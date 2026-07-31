package org.acme.order.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.entity.DiscountRate;
import org.acme.order.repository.DiscountRateRepository;

import java.math.BigDecimal;

@ApplicationScoped
public class DiscountCalculationService {

    @Inject
    DiscountRateRepository discountRateRepository;

    public BigDecimal calculateDiscount(BigDecimal total) {
        DiscountRate rate = discountRateRepository.find("minTotal <= ?1", total).firstResult();
        if (rate != null) {
            return total.multiply(BigDecimal.valueOf(rate.discountPercent))
                    .divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}
