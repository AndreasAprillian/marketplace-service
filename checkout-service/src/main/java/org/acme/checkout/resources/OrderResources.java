package org.acme.checkout.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.checkout.client.OrderServiceClient;
import org.acme.shared.security.JwtClaimExtractor;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/orders")
@SecurityRequirement(name = "SecurityScheme")
public class OrderResources {

    @Inject
    @RestClient
    OrderServiceClient orderServiceClient;

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("orderId") String orderId, @HeaderParam("Authorization") String authorization) {
        try {
            JwtClaimExtractor.extract(authorization);
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
        }
        return orderServiceClient.getOrder(orderId, authorization);
    }
}
