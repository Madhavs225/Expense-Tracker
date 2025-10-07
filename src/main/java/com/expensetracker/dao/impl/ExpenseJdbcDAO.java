package com.expensetracker.dao.impl;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.util.DBConnectionManager;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpenseJdbcDAO implements ExpenseDAO {

    private final DBConnectionManager connectionManager = DBConnectionManager.getInstance();

    private static final String INSERT_SQL = "INSERT INTO expense(category_id, expense_date, amount, payment_method, description) VALUES(?,?,?,?,?)";
    private static final String UPDATE_SQL = "UPDATE expense SET category_id=?, expense_date=?, amount=?, payment_method=?, description=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM expense WHERE id=?";
    private static final String FIND_BY_ID_SQL = "SELECT e.id, e.category_id, e.expense_date, e.amount, e.payment_method, e.description, e.created_at, e.updated_at, c.name, c.monthly_budget_limit, c.created_at AS c_created_at FROM expense e JOIN category c ON e.category_id = c.id WHERE e.id=?";
    private static final String BY_DATE_RANGE_SQL = "SELECT e.id, e.category_id, e.expense_date, e.amount, e.payment_method, e.description, e.created_at, e.updated_at, c.name, c.monthly_budget_limit, c.created_at AS c_created_at FROM expense e JOIN category c ON e.category_id = c.id WHERE e.expense_date BETWEEN ? AND ? ORDER BY e.expense_date";
    private static final String BY_CATEGORY_SQL = BY_DATE_RANGE_SQL + " AND e.category_id=?"; // appended differently when used
    private static final String SEARCH_SQL = "SELECT e.id, e.category_id, e.expense_date, e.amount, e.payment_method, e.description, e.created_at, e.updated_at, c.name, c.monthly_budget_limit, c.created_at AS c_created_at FROM expense e JOIN category c ON e.category_id = c.id WHERE e.expense_date BETWEEN ? AND ? AND LOWER(e.description) LIKE ? ORDER BY e.expense_date";
    private static final String LIST_RECENT_SQL = "SELECT e.id, e.category_id, e.expense_date, e.amount, e.payment_method, e.description, e.created_at, e.updated_at, c.name, c.monthly_budget_limit, c.created_at AS c_created_at FROM expense e JOIN category c ON e.category_id = c.id ORDER BY e.created_at DESC LIMIT ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM expense";

    @Override
    public Expense insert(Expense expense) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, expense.getCategory().getId());
            ps.setDate(2, Date.valueOf(expense.getDate()));
            ps.setBigDecimal(3, expense.getAmount());
            ps.setString(4, expense.getPaymentMethod().name());
            ps.setString(5, expense.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    Expense persisted = Expense.create(expense.getCategory(), expense.getDate(), expense.getAmount(), expense.getPaymentMethod(), expense.getDescription());
                    persisted.markPersisted(id, expense.getCreatedAt(), null);
                    return persisted;
                }
            }
            throw new RuntimeException("Failed to retrieve generated key for expense");
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting expense", e);
        }
    }

    @Override
    public boolean update(Expense expense) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            ps.setInt(1, expense.getCategory().getId());
            ps.setDate(2, Date.valueOf(expense.getDate()));
            ps.setBigDecimal(3, expense.getAmount());
            ps.setString(4, expense.getPaymentMethod().name());
            ps.setString(5, expense.getDescription());
            ps.setLong(6, expense.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating expense", e);
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expense", e);
        }
    }

    @Override
    public Optional<Expense> findById(long id) {
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expense by id", e);
        }
    }

    @Override
    public List<Expense> findByDateRange(LocalDate start, LocalDate end) {
        List<Expense> list = new ArrayList<>();
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(BY_DATE_RANGE_SQL)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying expenses by date range", e);
        }
        return list;
    }

    @Override
    public List<Expense> findByCategory(int categoryId, LocalDate start, LocalDate end) {
        List<Expense> list = new ArrayList<>();
        String sql = BY_DATE_RANGE_SQL + " AND e.category_id=?"; // adapt
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setInt(3, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying expenses by category", e);
        }
        return list;
    }

    @Override
    public List<Expense> search(String keyword, LocalDate start, LocalDate end) {
        List<Expense> list = new ArrayList<>();
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(SEARCH_SQL)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setString(3, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching expenses", e);
        }
        return list;
    }

    @Override
    public List<Expense> listRecent(int limit) {
        List<Expense> list = new ArrayList<>();
        try (Connection con = connectionManager.getConnection(); PreparedStatement ps = con.prepareStatement(LIST_RECENT_SQL)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing recent expenses", e);
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
            throw new RuntimeException("Error counting expenses", e);
        }
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        int categoryId = rs.getInt("category_id");
        String categoryName = rs.getString("name");
        java.math.BigDecimal limit = rs.getBigDecimal("monthly_budget_limit");
        java.sql.Date date = rs.getDate("expense_date");
        BigDecimal amount = rs.getBigDecimal("amount");
        PaymentMethod method = PaymentMethod.valueOf(rs.getString("payment_method"));
        String description = rs.getString("description");
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Category category = new Category(categoryId, categoryName, limit, rs.getTimestamp("c_created_at").toLocalDateTime());
        Expense e = Expense.create(category, date.toLocalDate(), amount, method, description);
        e.markPersisted(id, created.toLocalDateTime(), updated != null ? updated.toLocalDateTime() : null);
        return e;
    }
}
