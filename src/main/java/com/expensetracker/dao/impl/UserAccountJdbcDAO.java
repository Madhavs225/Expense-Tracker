package com.expensetracker.dao.impl;

import com.expensetracker.dao.UserAccountDAO;
import com.expensetracker.model.Role;
import com.expensetracker.model.UserAccount;
import com.expensetracker.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserAccountJdbcDAO implements UserAccountDAO {

    private final DBConnectionManager cm = DBConnectionManager.getInstance();

    private static final String INSERT_SQL = "INSERT INTO user_account(username, password_hash, salt, role) VALUES(?,?,?,?)";
    private static final String FIND_SQL = "SELECT id, username, password_hash, salt, role, active, created_at FROM user_account WHERE LOWER(username)=LOWER(?)";

    @Override
    public UserAccount insert(UserAccount user) {
        try (Connection con = cm.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user", e);
        }
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        try (Connection con = cm.getConnection(); PreparedStatement ps = con.prepareStatement(FIND_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String un = rs.getString("username");
                    String hash = rs.getString("password_hash");
                    String salt = rs.getString("salt");
                    Role role = Role.valueOf(rs.getString("role"));
                    boolean active = rs.getBoolean("active");
                    LocalDateTime created = rs.getTimestamp("created_at").toLocalDateTime();
                    return Optional.of(new UserAccount(id, un, hash, salt, role, active, created));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username", e);
        }
    }
}
