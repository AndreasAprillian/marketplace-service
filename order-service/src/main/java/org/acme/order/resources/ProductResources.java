package org.acme.order.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.order.entity.Product;

import java.util.List;

@Path("/products")
public class ProductResources {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> listProducts() {
        return Product.listAll();
    }
}
