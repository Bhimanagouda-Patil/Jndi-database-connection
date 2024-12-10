package com.unisys.service;

import com.unisys.dao.UserDao;
import com.unisys.model.User;
import com.unisys.security.RequiresAccessControl;
import com.unisys.errors.DaoException;
import com.unisys.errors.EmailServiceException;
import com.unisys.model.SystemMessage;
import com.unisys.controller.MessagePublisher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final EmailService emailService;
    private final MessagePublisher messagePublisher;

    public UserService(EmailService emailService, MessagePublisher messagePublisher) {
        if (emailService == null || messagePublisher == null) {
            throw new IllegalArgumentException("EmailService and MessagePublisher cannot be null");
        }
        this.userDao = new UserDao();
        this.emailService = emailService;
        this.messagePublisher = messagePublisher;
    }

    @RequiresAccessControl(role = "ADMIN")
    public String createUser(User user) {
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and its properties (username, email) cannot be null");
        }
        try {
            userDao.createUser(user);
            if (user.getEmail() != null) {
                emailService.sendEmail(user.getEmail(), "Welcome to the System", "Dear " + user.getUsername() + ",\nWelcome to our system!");
            }
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setSource("UserService");
            systemMessage.setMessage("New user created: " + user.getUsername());
            if (messagePublisher != null) {
                messagePublisher.publishMessage(systemMessage);
            }
            return "User created successfully and notifications sent.";
        } catch (SecurityException e) {
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied", e); 
        } catch (DaoException e) {
            logger.error("Database error creating user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user in database", e);
        } catch (EmailServiceException e) {
            logger.error("Error sending email: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email", e);
        } catch (Exception e) {
            logger.error("Unexpected error creating user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error creating user", e);
        }
    }

    @RequiresAccessControl(role = "ADMIN")
    public boolean deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        try {
            return userDao.deleteUser(id);
        } catch (SecurityException e) {
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied", e); 
        } catch (DaoException e) {
            logger.error("Database error deleting user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user in database", e);
        } catch (Exception e) {
            logger.error("Unexpected error deleting user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error deleting user", e);
        }
    }

    public boolean updateUser(Long id, User user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and User cannot be null");
        }
        try {
            return userDao.updateUser(id, user);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update user", e);
        }
    }

    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        try {
            return userDao.getUserById(id);
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve user", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            return userDao.getAllUsers();
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve users", e);
        }
    }
}