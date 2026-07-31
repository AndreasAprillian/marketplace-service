package org.acme.customer.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.customer.service.CustomerService;
import org.acme.shared.dto.CustomerResponse;

@Path("/customers")
public class CustomerController {

    @Inject
    CustomerService customerService;

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("username") String username) {
        try {
            CustomerResponse response = customerService.getCustomer(username);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
