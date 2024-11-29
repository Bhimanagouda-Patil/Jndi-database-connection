package com.unisys.dao;

import com.unisys.model.User;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for managing user data in the database.
 * <p>
 * This class provides methods for interacting with the users table in the database.
 * It includes functionality to retrieve, create, update, and delete user records.
 * </p>
 */
public class UserDao {

    private static final Logger logger = Logger.getLogger(UserDao.class.getName());
    private DataSource dataSource;

    /**
     * Initializes the DataSource by looking up the JNDI resource.
     * Logs an error if initialization fails.
     */
    public UserDao() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/comp/env/jdbc/student");
            logger.log(Level.INFO, "DataSource initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize DataSource. Application might not function correctly.", e);
        }
    }

    /**
     * Retrieves all users from the database.
     * <p>
     * Executes a SELECT query to fetch all user records from the users table.
     * </p>
     *
     * @return a list of {@link User} objects representing all users.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            logger.log(Level.INFO, "Executing query to retrieve all users: {0}", query);
            while (rs.next()) {
                users.add(new User(rs.getLong("id"), rs.getString("username"), rs.getString("email")));
            }
            logger.log(Level.INFO, "Successfully retrieved {0} users from the database.", users.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to retrieve users. Query: " + query, e);
        }
        return users;
    }

    /**
     * Retrieves a user by its ID.
     * <p>
     * Executes a SELECT query to fetch a single user by its ID.
     * </p>
     *
     * @param id the ID of the user to be retrieved.
     * @return a {@link User} object if found, or {@code null} if not found.
     */
    public User getUserById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);
            logger.log(Level.INFO, "Executing query to retrieve user with ID: {0}", id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.log(Level.INFO, "User with ID: {0} retrieved successfully.", id);
                return new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"));
            } else {
                logger.log(Level.WARNING, "User with ID: {0} not found.", id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to retrieve user with ID: " + id, e);
        }
        return null;
    }

    /**
     * Creates a new user in the database.
     * <p>
     * Executes an INSERT query to create a new user record in the users table.
     * </p>
     *
     * @param user the user object containing the data to be saved.
     */
    public void createUser(User user) {
        String query = "INSERT INTO users (username, email) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            logger.log(Level.INFO, "Executing query to create user: {0}", user);
            ps.executeUpdate();
            logger.log(Level.INFO, "User created successfully: {0}", user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create user: " + user, e);
        }
    }

    /**
     * Updates an existing user by ID.
     * <p>
     * Executes an UPDATE query to modify a user's details in the database.
     * </p>
     *
     * @param id   the ID of the user to be updated.
     * @param user the user object containing the new data.
     * @return {@code true} if the user was successfully updated, {@code false} otherwise.
     */
    public boolean updateUser(Long id, User user) {
        String query = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setLong(3, id);
            logger.log(Level.INFO, "Executing query to update user with ID: {0}, New Data: {1}", new Object[]{id, user});

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                logger.log(Level.INFO, "User with ID: {0} updated successfully.", id);
                return true;
            } else {
                logger.log(Level.WARNING, "User with ID: {0} not found. Update failed.", id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update user with ID: " + id, e);
        }
        return false;
    }

    /**
     * Deletes a user by ID.
     * <p>
     * Executes a DELETE query to remove a user from the users table.
     * </p>
     *
     * @param id the ID of the user to be deleted.
     * @return {@code true} if the user was successfully deleted, {@code false} otherwise.
     */
    public boolean deleteUser(Long id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);
            logger.log(Level.INFO, "Executing query to delete user with ID: {0}", id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                logger.log(Level.INFO, "User with ID: {0} deleted successfully.", id);
                return true;
            } else {
                logger.log(Level.WARNING, "User with ID: {0} not found. Deletion failed.", id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to delete user with ID: " + id, e);
        }
        return false;
    }
}
