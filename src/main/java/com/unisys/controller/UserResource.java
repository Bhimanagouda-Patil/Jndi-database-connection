package com.unisys.controller;

import com.unisys.model.User;
import com.unisys.service.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * RESTful web service resource for managing users.
 * <p>
 * This class provides the following endpoints:
 * <ul>
 *   <li>GET /users - Retrieves all users</li>
 *   <li>GET /users/{id} - Retrieves a user by ID</li>
 *   <li>POST /users - Creates a new user</li>
 *   <li>PUT /users/{id} - Updates an existing user by ID</li>
 *   <li>DELETE /users/{id} - Deletes a user by ID</li>
 * </ul>
 * Each endpoint interacts with the {@link UserService} to perform operations on the user data.
 * </p>
 */
@Path("/users")
@Component
public class UserResource {

	private UserService userService;

	public UserResource(UserService userService) {
        this.userService = userService;
    }
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";

    /**
     * Retrieves all users.
     * <p>
     * This method calls the {@link UserService#getAllUsers()} method to retrieve a list of all users.
     * </p>
     *
     * @return a list of {@link User} objects in JSON format.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a user by ID.
     * <p>
     * This method calls the {@link UserService#getUserById(Long)} method to retrieve a user by its ID.
     * If the user is found, it returns a {@link Response} with status 200 (OK) and the user details.
     * If the user is not found, it returns a 404 (Not Found) response with an error message.
     * </p>
     *
     * @param id the ID of the user to be retrieved.
     * @return a {@link Response} containing the user or an error message.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
    }

    /**
     * Creates a new user.
     * <p>
     * This method accepts a {@link User} object in JSON format, calls the {@link UserService#createUser(User)}
     * method to create the user, and returns a 201 (Created) response with the result.
     * </p>
     *
     * @param user the user object to be created.
     * @return a {@link Response} with the creation status.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        String result = userService.createUser(user);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Updates an existing user by ID.
     * <p>
     * This method calls the {@link UserService#updateUser(Long, User)} method to update the user details.
     * If the update is successful, it returns a 200 (OK) response with a success message.
     * If the user is not found, it returns a 404 (Not Found) response with an error message.
     * </p>
     *
     * @param id   the ID of the user to be updated.
     * @param user the updated user object.
     * @return a {@link Response} with the update status.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, User user) {
        boolean updated = userService.updateUser(id, user);
        if (updated) {
            return Response.ok("User updated successfully").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
    }

    /**
     * Deletes the user by ID.
     * <p>
     * This method calls the {@link UserService#deleteUser(Long)} method to delete the user by ID.
     * If the deletion is successful, it returns a 200 (OK) response with a success message.
     * If the user is not found, it returns a 404 (Not Found) response with an error message.
     * </p>
     *
     * @param id the ID of the user to be deleted.
     * @return a {@link Response} with the deletion status.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Response.ok("User deleted successfully").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(USER_NOT_FOUND_MESSAGE).build();
    }
}
