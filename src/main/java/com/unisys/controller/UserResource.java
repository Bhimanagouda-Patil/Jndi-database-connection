package com.unisys.controller;

import java.util.List;

import com.unisys.dao.UserDao;
import com.unisys.model.User;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



@Path("/users")
public class UserResource {

    private final UserDao userDao = new UserDao();
    public static final String S="User not found";
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        User user = userDao.getUserById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(S).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        userDao.createUser(user);
        return Response.status(Response.Status.CREATED).entity("User created successfully").build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, User user) {
        boolean updated = userDao.updateUser(id, user);
        if (updated) {
            return Response.ok("User updated successfully").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(S).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userDao.deleteUser(id);
        if (deleted) {
            return Response.ok("User deleted successfully").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(S).build();
    }
}