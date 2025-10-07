package com.expensetracker.dao.impl;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.model.Category;
import com.expensetracker.util.DBConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryJdbcDAO implements CategoryDAO {

    private final DBConnectionManager connectionManager = DBConnectionManager.getInstance();

    private static final String INSERT_SQL = "INSERT INTO category(name, monthly_budget_limit) VALUES(?, ?)";
    private static final String UPDATE_SQL = "UPDATE category SET name = ?, monthly_budget_limit = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM category WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, monthly_budget_limit, created_at FROM category WHERE id = ?";
    private static final String FIND_BY_NAME_SQL = "SELECT id, name, monthly_budget_limit, created_at FROM category WHERE LOWER(name) = LOWER(?)";
    private static final String FIND_ALL_SQL = "SELECT id, name, monthly_budget_limit, created_at FROM category ORDER BY name";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM category";

    @Override
    public Category insert(Category category) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            if (category.getMonthlyBudgetLimit() == null) {
                ps.setNull(2, Types.DECIMAL);
            } else {
                ps.setBigDecimal(2, category.getMonthlyBudgetLimit());
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    category.setId(rs.getInt(1));
                }
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting category", e);
        }
    }

    @Override
    public boolean update(Category category) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, category.getName());
            if (category.getMonthlyBudgetLimit() == null) {
                ps.setNull(2, Types.DECIMAL);
            } else {
                ps.setBigDecimal(2, category.getMonthlyBudgetLimit());
            }
            ps.setInt(3, category.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    @Override
    public Optional<Category> findById(int id) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by id", e);
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(FIND_BY_NAME_SQL)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by name", e);
        }
    }

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(FIND_ALL_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing categories", e);
        }
        return list;
    }

    @Override
    public long count() {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(COUNT_SQL); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting categories", e);
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        java.math.BigDecimal limit = rs.getBigDecimal("monthly_budget_limit");
        Timestamp created = rs.getTimestamp("created_at");
        return new Category(id, name, limit, created.toLocalDateTime());
    }
}
