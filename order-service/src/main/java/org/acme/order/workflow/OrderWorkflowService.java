package org.acme.order.workflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.order.service.*;
import org.acme.shared.dto.CheckoutRequest;

import java.math.BigDecimal;

@ApplicationScoped
public class OrderWorkflowService {

    @Inject
    CartValidationService cartValidationService;

    @Inject
    StockService stockService;

    @Inject
    ShippingService shippingService;

    @Inject
    DiscountCalculationService discountCalculationService;

    @Inject
    OrderCreationService orderCreationService;

    @Inject
    PaymentService paymentService;

    @Inject
    OrderFailedService orderFailedService;

    @Inject
    SubTotalCalculationService subTotalCalculationService;

    @Inject
    OrderValidationService orderValidationService;

    public void processOrder(CheckoutRequest request) {
        try {
            cartValidationService.validateCart(request);

            boolean stockAvailable = stockService.checkStock(request.getItems());
            if (!stockAvailable) {
                orderFailedService.orderProcesFailed(request);
                return;
            }

            BigDecimal subtotal = subTotalCalculationService.calculateSubtotal(request);
            BigDecimal shippingCost = shippingService.calculateShipping("DEFAULT");
            BigDecimal discount = discountCalculationService.calculateDiscount(subtotal);
            BigDecimal total = subtotal.add(shippingCost).subtract(discount);

            if (!orderValidationService.isOrderExists(request.getOrderId())){
                orderCreationService.createOrder(
                        request.getOrderId(), request.getItems(), request.getCustomerUsername(),
                        request.getEmail(), request.getPaymentMethod(),
                        subtotal, shippingCost, discount, total
                );
                System.out.println("success save order");
            }

            paymentService.processPayment(request.getOrderId(), total, request.getPaymentMethod(),
                    request.getCustomerUsername(), request.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("process order event failed", e);
        }
    }
}
