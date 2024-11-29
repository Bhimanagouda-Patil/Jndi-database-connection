package com.unisys.service;

import com.unisys.dao.UserDao;
import com.unisys.model.User;

import java.util.List;

/**
 * Service layer for managing user operations.
 * <p>
 * The {@link UserService} class acts as a middle layer between the controller and the DAO.
 * It provides methods for retrieving, creating, updating, and deleting users.
 * </p>
 */
public class UserService {

    private final UserDao userDao;

    /**
     * Constructs a new instance of {@link UserService} and initializes the {@link UserDao}.
     */
    public UserService() {
        this.userDao = new UserDao();
    }

    /**
     * Retrieves all users.
     * 
     * @return a list of {@link User} objects representing all users.
     */
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * Retrieves a user by its ID.
     * 
     * @param id the ID of the user to retrieve.
     * @return the {@link User} object if found, {@code null} otherwise.
     */
    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    /**
     * Creates a new user.
     * 
     * @param user the {@link User} object to create.
     * @return a message indicating the result of the operation.
     */
    public String createUser(User user) {
        userDao.createUser(user);
        return "User created successfully";
    }

    /**
     * Updates an existing user by its ID.
     * 
     * @param id   the ID of the user to update.
     * @param user the {@link User} object containing the updated data.
     * @return {@code true} if the user was successfully updated, {@code false} otherwise.
     */
    public boolean updateUser(Long id, User user) {
        return userDao.updateUser(id, user);
    }

    /**
     * Deletes a user by its ID.
     * 
     * @param id the ID of the user to delete.
     * @return {@code true} if the user was successfully deleted, {@code false} otherwise.
     */
    public boolean deleteUser(Long id) {
        return userDao.deleteUser(id);
    }
}
