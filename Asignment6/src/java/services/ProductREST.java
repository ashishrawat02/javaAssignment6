/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import entities.Product;
import entities.ProductList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author c0649005
 */
@Path("/product")
@RequestScoped
public class ProductREST {

    @Inject
    ProductList productlist;

    @GET
    @Produces("application/json")
    public Response getAll() {
        return Response.ok(productlist.toJSON()).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getById(@PathParam("id") int id) {
        return Response.ok(productlist.get(id).toJSON()).build();
    }

    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response response;
        try {
            productlist.add(new Product(json));
            response = Response.ok(productlist.get(json.getInt("productId")).toJSON()).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
        return response;
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response set(@PathParam("id") int id, JsonObject json) {

        try {
            Product p = new Product(json);
            productlist.set(id, p);
            return Response.ok(productlist.get(id).toJSON()).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }

    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        try {

            productlist.remove(id);
            return Response.ok("Data deleted, Trust me.").build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }
}
