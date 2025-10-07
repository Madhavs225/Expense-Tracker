package com.expensetracker.controller;

import java.time.LocalDate;

import com.expensetracker.dao.impl.CategoryJdbcDAO;
import com.expensetracker.dao.impl.ExpenseJdbcDAO;
import com.expensetracker.report.DailyReport;
import com.expensetracker.report.DailyReportGenerator;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;

/**
 * Central application controller wiring services (basic bootstrap placeholder).
 */
public class AppController {

    private final CategoryService categoryService;
    private final ExpenseService expenseService;
    private final DailyReportGenerator dailyReportGenerator;

    public AppController() {
        this.categoryService = new CategoryService(new CategoryJdbcDAO());
        ExpenseJdbcDAO expenseDAO = new ExpenseJdbcDAO();
        this.expenseService = new ExpenseService(expenseDAO);
        this.dailyReportGenerator = new DailyReportGenerator(expenseDAO);
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public ExpenseService getExpenseService() {
        return expenseService;
    }

    public DailyReport generateDailyReport(LocalDate date) {
        return dailyReportGenerator.generate(date);
    }

    // Convenience methods for UI
    public java.util.List<com.expensetracker.model.Category> getAllCategories() {
        return categoryService.listCategories();
    }

    public void addCategory(com.expensetracker.model.Category category) {
        categoryService.createCategory(category.getName(), category.getMonthlyBudgetLimit());
    }

    public java.util.List<com.expensetracker.model.Expense> getAllExpenses() {
        return expenseService.listRecent(100); // Get recent 100 expenses
    }

    public void addExpense(com.expensetracker.model.Expense expense) {
        expenseService.addExpense(expense.getCategory(), expense.getDate(),
                expense.getAmount(), expense.getPaymentMethod(), expense.getDescription());
    }
}
