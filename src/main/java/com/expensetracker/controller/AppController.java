package com.expensetracker.controller;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import com.expensetracker.dao.impl.CategoryJdbcDAO;
import com.expensetracker.dao.impl.ExpenseJdbcDAO;
import com.expensetracker.dao.impl.UserAccountJdbcDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.report.*;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.AuthService;
import com.expensetracker.background.BackgroundTaskManager;
import com.expensetracker.background.BudgetAlertService;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;

/**
 * Enhanced central application controller with background services and file
 * management
 */
public class AppController {

    // Services
    private final CategoryService categoryService;
    private final ExpenseService expenseService;
    private final AuthService authService;

    // Background services
    private final BackgroundTaskManager taskManager;
    private final BudgetAlertService budgetAlertService;

    // Report generators
    private final DailyReportGenerator dailyReportGenerator;
    private final WeeklyReportGenerator weeklyReportGenerator;

    // File management
    private final FileManager fileManager;

    // Application state
    private boolean backgroundServicesStarted = false;

    public AppController() {
        LoggerUtil.info("Initializing enhanced AppController");

        // Initialize file manager
        this.fileManager = FileManager.getInstance();

        // Initialize DAOs
        CategoryJdbcDAO categoryDAO = new CategoryJdbcDAO();
        ExpenseJdbcDAO expenseDAO = new ExpenseJdbcDAO();
        UserAccountJdbcDAO userAccountDAO = new UserAccountJdbcDAO();

        // Initialize services
        this.categoryService = new CategoryService(categoryDAO);
        this.expenseService = new ExpenseService(expenseDAO);
        this.authService = new AuthService(userAccountDAO);

        // Initialize background services
        this.taskManager = BackgroundTaskManager.getInstance();
        this.budgetAlertService = new BudgetAlertService(categoryDAO, expenseDAO);

        // Initialize report generators
        this.dailyReportGenerator = new DailyReportGenerator(expenseDAO);
        this.weeklyReportGenerator = new WeeklyReportGenerator(expenseDAO);

        LoggerUtil.info("AppController initialized successfully");
    }

    /**
     * Start background services
     */
    public void startBackgroundServices() {
        if (!backgroundServicesStarted) {
            LoggerUtil.info("Starting background services");

            // Start budget monitoring
            budgetAlertService.startMonitoring();

            // Schedule periodic file cleanup
            taskManager.schedule(() -> {
                LoggerUtil.info("Running scheduled file cleanup");
                fileManager.cleanupOldFiles(fileManager.getReportsDir(), 30);
                fileManager.cleanupOldFiles(fileManager.getLogsDir(), 7);
            }, 1, java.util.concurrent.TimeUnit.HOURS);

            backgroundServicesStarted = true;
            LoggerUtil.info("Background services started successfully");
        }
    }

    /**
     * Stop background services gracefully
     */
    public void stopBackgroundServices() {
        if (backgroundServicesStarted) {
            LoggerUtil.info("Stopping background services");

            budgetAlertService.stopMonitoring();
            taskManager.shutdown();

            backgroundServicesStarted = false;
            LoggerUtil.info("Background services stopped");
        }
    }

    // Service getters
    public CategoryService getCategoryService() {
        return categoryService;
    }

    public ExpenseService getExpenseService() {
        return expenseService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public BudgetAlertService getBudgetAlertService() {
        return budgetAlertService;
    }

    // Report generation
    public DailyReport generateDailyReport(LocalDate date) {
        DailyReportRequest request = new DailyReportRequest(date);
        return dailyReportGenerator.generate(request);
    }

    public WeeklyReport generateWeeklyReport(LocalDate startDate, LocalDate endDate) {
        WeeklyReportRequest request = new WeeklyReportRequest(startDate, endDate);
        return weeklyReportGenerator.generate(request);
    }

    public void exportDailyReportToCsv(DailyReport report) {
        CsvExportStrategy exportStrategy = new CsvExportStrategy();

        taskManager.execute(() -> {
            try {
                String filename = fileManager.createTimestampedFilename("daily_report", "csv");
                java.nio.file.Path filePath = fileManager.createReportPath(filename);
                exportStrategy.exportToFile(report, filePath);
                LoggerUtil.info("Daily report exported to: " + filePath);
            } catch (Exception e) {
                LoggerUtil.error("Failed to export daily report", e);
            }
        });
    }

    public void exportWeeklyReportToCsv(WeeklyReport report) {
        CsvExportStrategy exportStrategy = new CsvExportStrategy();
        weeklyReportGenerator.exportReport(report, exportStrategy);
    }

    // Enhanced convenience methods for UI
    public List<Category> getAllCategories() {
        return categoryService.listCategories();
    }

    public Category addCategory(String name, BigDecimal monthlyLimit) {
        Category category = categoryService.createCategory(name, monthlyLimit);

        // Trigger budget check if this category has a limit
        if (monthlyLimit != null && monthlyLimit.compareTo(BigDecimal.ZERO) > 0) {
            budgetAlertService.checkBudgetLimitsNow();
        }

        return category;
    }

    public void addCategory(Category category) {
        categoryService.createCategory(category.getName(), category.getMonthlyBudgetLimit());
    }

    public List<Expense> getAllExpenses() {
        return expenseService.listRecent(100);
    }

    public Expense addExpense(Category category, LocalDate date, BigDecimal amount, PaymentMethod paymentMethod, String description) {
        Expense expense = expenseService.addExpense(category, date, amount, paymentMethod, description);

        // Trigger budget check after adding expense
        budgetAlertService.checkBudgetLimitsNow();

        return expense;
    }

    public void addExpense(Expense expense) {
        expenseService.addExpense(expense.getCategory(), expense.getDate(),
                expense.getAmount(), expense.getPaymentMethod(), expense.getDescription());

        // Trigger budget check after adding expense
        budgetAlertService.checkBudgetLimitsNow();
    }

    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseService.findByDateRange(startDate, endDate);
    }

    public boolean isBackgroundServicesStarted() {
        return backgroundServicesStarted;
    }
}
