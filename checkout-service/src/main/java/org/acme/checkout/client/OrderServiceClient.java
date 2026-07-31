package org.acme.checkout.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@RegisterRestClient(configKey = "order-processing-api")
public interface OrderServiceClient {

    @GET
    @Path("/orders/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getOrder(@PathParam("orderId") String orderId, @HeaderParam("Authorization") String authorization);

    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    List<Map<String, Object>> listProducts();
}
