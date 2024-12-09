package com.unisys.dao;

import com.unisys.model.User;
import com.unisys.errors.DaoException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {

    private static final Logger logger = Logger.getLogger(UserDao.class.getName());
    private DataSource dataSource;

    public UserDao() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/comp/env/jdbc/student");
            logger.log(Level.INFO, "DataSource initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize DataSource. Application might not function correctly.", e);
        }
    }

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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve users. Query: " + query, e);
            throw new DaoException("Failed to retrieve users", e);
        }
        return users;
    }


    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
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
            throw new DaoException("Failed to retrieve user with ID: " + id, e);
        }
        return null;
    }

    public void createUser(User user) {
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and its properties cannot be null");
        }

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
            throw new DaoException("Failed to create user", e);
        }
    }

    public boolean updateUser(Long id, User user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and User cannot be null");
        }

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
            throw new DaoException("Failed to update user", e);
        }
        return false;
    }

    public boolean deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

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
            throw new DaoException("Failed to delete user", e);
        }
        return false;
    }
}
