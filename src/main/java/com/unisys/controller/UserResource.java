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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import com.unisys.security.RequiresAccessControl;

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
   @RequiresAccessControl(role = "ADMIN")  // This triggers the security aspect
    public Response createUser(@Valid User user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and email are required.")
                    .build();
        }
        try {
            String result = userService.createUser(user);
            return Response.status(Response.Status.CREATED).entity(result).build();
        } catch (SecurityException e) {
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied", e); 
        }catch (IllegalArgumentException e) {
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
  @RequiresAccessControl(role = "ADMIN")  // This triggers the security aspect
    public Response deleteUser(@PathParam("id") @NotNull Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return Response.ok("User deleted successfully").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
        }catch (SecurityException e) {
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied", e); }
        catch (Exception e) {
            return handleInternalError(e);
        }
    }

    // Global error handling for internal errors and security exceptions
    private Response handleInternalError(Exception e) {
        logger.error("Unhandled error: {}", e.getMessage(), e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(INTERNAL_SERVER_ERROR_MESSAGE)
                .build();
    }

    // Custom handler for access control (SecurityException will be caught)
    @ExceptionHandler(SecurityException.class)
    public Response handleSecurityException(SecurityException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                .entity(ex.getMessage())
                .build();
    }

    // Custom handler for other exceptions (ResponseStatusException)
    @ExceptionHandler(ResponseStatusException.class)
    private Response handleResponseStatusException(ResponseStatusException e) {
        logger.error("Response status exception: {}", e.getMessage(), e);

        // Convert the Spring HttpStatus to a numeric status code
        int statusCode = e.getStatusCode().value();  // This gives the numeric value (e.g., 403)

        return Response.status(statusCode) // Use the numeric value directly
                .entity(e.getReason()) // Provide the reason message
                .build();
    }

}
