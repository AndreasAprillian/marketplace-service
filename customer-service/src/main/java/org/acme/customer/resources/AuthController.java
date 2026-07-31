package org.acme.customer.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.customer.dto.LoginRequest;
import org.acme.customer.dto.LoginResponse;
import org.acme.customer.dto.RegisterRequest;
import org.acme.customer.service.AuthService;

@Path("/auth")
public class AuthController {

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest request) {
        try {
            authService.register(request);
            return Response.status(Response.Status.CREATED).entity("Customer registered").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }
}
