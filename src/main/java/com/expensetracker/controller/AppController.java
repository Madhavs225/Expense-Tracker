package com.expensetracker.controller;

import com.expensetracker.dao.impl.CategoryJdbcDAO;
import com.expensetracker.dao.impl.ExpenseJdbcDAO;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.report.DailyReportGenerator;
import com.expensetracker.report.DailyReport;
import com.expensetracker.dao.impl.ExpenseJdbcDAO;
import java.time.LocalDate;

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
}
