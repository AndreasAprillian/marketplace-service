package org.acme.checkout.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.checkout.service.CheckoutService;
import org.acme.shared.dto.CheckoutRequest;
import org.acme.shared.security.JwtClaimExtractor;

import java.util.UUID;

@Slf4j
@Path("/checkout")
public class CheckoutResource {

    @Inject
    CheckoutService checkoutService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkout(CheckoutRequest request, @HeaderParam("Authorization") String authorization) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        JwtClaimExtractor.ExtractedClaims claims;
        try {
            claims = JwtClaimExtractor.extract(authorization);
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing or invalid Authorization header")
                    .build();
        }
        CheckoutRequest modified = CheckoutRequest.builder()
                .orderId(UUID.randomUUID().toString())
                .items(request.getItems())
                .paymentMethod(request.getPaymentMethod())
                .customerUsername(claims.getUsername())
                .email(claims.getEmail())
                .build();
        checkoutService.processCheckout(modified);
        log.warn("OrderId ="+modified.getOrderId()+" received for customer: " + claims.getUsername());
        return Response.accepted().entity("OrderId ="+modified.getOrderId()+" received for customer: " + claims.getUsername()).build();
    }
}
