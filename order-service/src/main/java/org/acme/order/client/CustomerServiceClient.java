package org.acme.order.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.shared.dto.CustomerResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "customer-api")
public interface CustomerServiceClient {

    @GET
    @Path("/customers/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    CustomerResponse getCustomer();
}
