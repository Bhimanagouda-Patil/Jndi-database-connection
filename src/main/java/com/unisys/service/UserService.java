package com.unisys.service;

import com.unisys.dao.UserDao;
import com.unisys.model.User;
import com.unisys.model.SystemMessage;
import com.unisys.controller.MessagePublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing user operations.
 * <p>
 * The {@link UserService} class acts as a middle layer between the controller and the DAO.
 * It provides methods for retrieving, creating, updating, and deleting users.
 * Additionally, it integrates with email service to notify users and message publisher to send system messages.
 * </p>
 */
@Service
public class UserService {

    private final UserDao userDao;
    private final EmailService emailService;
    private final MessagePublisher messagePublisher;

    public UserService(EmailService emailService, MessagePublisher messagePublisher) {
        this.userDao = new UserDao();
        this.emailService = emailService;
        this.messagePublisher = messagePublisher;
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
     * <p>
     * This method will insert the user into the database through the {@link UserDao}.
     * After the user is created, it sends an email to the user and publishes a system message.
     * </p>
     * 
     * @param user the {@link User} object to create.
     * @return a message indicating the result of the operation.
     */
    public String createUser(User user) {
        // Create the user in the database
        userDao.createUser(user);

        // Send a welcome email
        emailService.sendEmail(user.getEmail(), "Welcome to the System", "Dear " + user.getUsername() + ",\nWelcome to our system!");

        // Publish a system message
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setSource("UserService");
        systemMessage.setMessage("New user created: " + user.getUsername());
        messagePublisher.publishMessage(systemMessage);

        return "User created successfully and notifications sent.";
    }

    /**
     * Updates an existing user by its ID.
     * <p>
     * After the user is updated, it sends an email to notify the user and publishes a system message.
     * </p>
     * 
     * @param id   the ID of the user to update.
     * @param user the {@link User} object containing the updated data.
     * @return {@code true} if the user was successfully updated, {@code false} otherwise.
     */
    public boolean updateUser(Long id, User user) {
        boolean updated = userDao.updateUser(id, user);

        if (updated) {
            // Send a notification email
            emailService.sendEmail(user.getEmail(), "Your Account has been Updated", "Dear " + user.getUsername() + ",\nYour account details have been updated.");

            // Publish a system message
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setSource("UserService");
            systemMessage.setMessage("User updated: " + user.getUsername());
            messagePublisher.publishMessage(systemMessage);
        }

        return updated;
    }

    /**
     * Deletes a user by its ID.
     * <p>
     * After the user is deleted, it sends an email to notify the user and publishes a system message.
     * </p>
     * 
     * @param id the ID of the user to delete.
     * @return {@code true} if the user was successfully deleted, {@code false} otherwise.
     */
    public boolean deleteUser(Long id) {
        User user = userDao.getUserById(id);
        boolean deleted = userDao.deleteUser(id);

        if (deleted && user != null) {
            // Send a deletion notification email
            emailService.sendEmail(user.getEmail(), "Account Deletion Confirmation", "Dear " + user.getUsername() + ",\nYour account has been successfully deleted.");

            // Publish a system message
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setSource("UserService");
            systemMessage.setMessage("User deleted: " + user.getUsername());
            messagePublisher.publishMessage(systemMessage);
        }

        return deleted;
    }
}
