package com.expensetracker.service;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer orchestrating expense validation and DAO operations.
 */
public class ExpenseService {

    private final ExpenseDAO expenseDAO;

    public ExpenseService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public Expense addExpense(Category category, LocalDate date, BigDecimal amount, PaymentMethod method, String description) {
        Expense expense = Expense.create(category, date, amount, method, description);
        return expenseDAO.insert(expense);
    }

    public boolean updateExpense(Expense expense) {
        return expenseDAO.update(expense);
    }

    public boolean deleteExpense(long id) {
        return expenseDAO.delete(id);
    }

    public List<Expense> listRecent(int limit) {
        return expenseDAO.listRecent(limit);
    }

    public List<Expense> findByDateRange(LocalDate start, LocalDate end) {
        return expenseDAO.findByDateRange(start, end);
    }

    public List<Expense> findByCategory(int categoryId, LocalDate start, LocalDate end) {
        return expenseDAO.findByCategory(categoryId, start, end);
    }

    public List<Expense> search(String keyword, LocalDate start, LocalDate end) {
        return expenseDAO.search(keyword, start, end);
    }

    public Optional<Expense> findById(long id) {
        return expenseDAO.findById(id);
    }
}
