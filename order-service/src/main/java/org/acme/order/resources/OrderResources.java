package org.acme.order.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.order.entity.OrderEntity;
import org.acme.order.repository.OrderItemRepository;
import org.acme.shared.security.JwtClaimExtractor;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.Map;

@Path("/orders")
@SecurityRequirement(name = "BearerAuth")
public class OrderResources {

    @Inject
    OrderItemRepository orderItemRepository;

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("orderId") String orderId, @HeaderParam("Authorization") String authorization) {
        JwtClaimExtractor.ExtractedClaims claims;
        try {
            claims = JwtClaimExtractor.extract(authorization);
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
        }

        OrderEntity order = OrderEntity.findById(orderId);
        if (order == null || !claims.getUsername().equals(order.customerUsername)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var items = orderItemRepository.findByOrderId(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.orderId);
        result.put("customerUsername", order.customerUsername);
        result.put("email", order.email);
        result.put("shippingCost", order.shippingCost);
        result.put("discount", order.discount);
        result.put("total", order.total);
        result.put("paymentMethod", order.paymentMethod);
        result.put("paymentStatus", order.paymentStatus);
        result.put("status", order.status);
        result.put("items", items);

        return Response.ok(result).build();
    }
}
