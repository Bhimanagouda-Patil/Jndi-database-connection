package com.unisys.controller;

import com.unisys.model.User;
import com.unisys.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/users")
@Component
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);
    private final UserService userService;

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An internal error occurred";
    private static final String VALIDATION_ERROR_MESSAGE = "Validation error occurred: ";

    public UserResource(UserService userService) {
        if (userService == null) {
            throw new IllegalArgumentException("UserService cannot be null");
        }
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            return Response.ok(userService.getAllUsers()).build();
        } catch (Exception e) {
            return handleInternalError(e);
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") @NotNull Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return Response.ok(user).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
        } catch (Exception e) {
            return handleInternalError(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid User user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and email are required.")
                    .build();
        }
        try {
            String result = userService.createUser(user);
            return Response.status(Response.Status.CREATED).entity(result).build();
        } catch (IllegalArgumentException e) {
            logger.error(VALIDATION_ERROR_MESSAGE, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return handleInternalError(e);
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") @NotNull Long id, @Valid User user) {
        try {
            boolean updated = userService.updateUser(id, user);
            if (updated) {
                return Response.ok("User updated successfully").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
        } catch (Exception e) {
            return handleInternalError(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") @NotNull Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return Response.ok("User deleted successfully").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
        } catch (Exception e) {
            return handleInternalError(e);
        }
    }

    private Response handleInternalError(Exception e) {
        logger.error("Unhandled error: {}", e.getMessage(), e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(INTERNAL_SERVER_ERROR_MESSAGE)
                .build();
    }
}
