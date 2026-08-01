package org.acme.checkout.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.checkout.client.OrderServiceClient;
import org.acme.shared.security.JwtClaimExtractor;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/products")
@SecurityRequirement(name = "SecurityScheme")
public class ProductResources {

    @Inject
    @RestClient
    OrderServiceClient orderServiceClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProducts(@HeaderParam("Authorization") String authorization) {
        try {
            JwtClaimExtractor.extract(authorization);
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
        }
        var products = orderServiceClient.listProducts();
        return Response.ok(products).build();
    }
}
