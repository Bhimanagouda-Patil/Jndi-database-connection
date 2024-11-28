package com.unisys.dao;

import javax.sql.DataSource;

import com.unisys.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;

public class UserDao {

    private DataSource dataSource;

    public UserDao() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/comp/env/jdbc/student");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(new User(rs.getLong("id"), rs.getString("username"), rs.getString("email")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createUser(User user) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO users (username, email) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateUser(Long id, User user) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "UPDATE users SET username = ?, email = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setLong(3, id);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "DELETE FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, id);
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}