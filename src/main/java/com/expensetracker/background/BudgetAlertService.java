package com.expensetracker.background;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.util.LoggerUtil;

/**
 * Background service for monitoring budget limits and sending alerts
 */
public class BudgetAlertService {

    private final CategoryDAO categoryDAO;
    private final ExpenseDAO expenseDAO;
    private final BackgroundTaskManager taskManager;
    private ScheduledFuture<?> alertTask;

    // Alert thresholds
    private static final double WARNING_THRESHOLD = 0.8; // 80%
    private static final double CRITICAL_THRESHOLD = 0.95; // 95%

    public BudgetAlertService(CategoryDAO categoryDAO, ExpenseDAO expenseDAO) {
        this.categoryDAO = categoryDAO;
        this.expenseDAO = expenseDAO;
        this.taskManager = BackgroundTaskManager.getInstance();
    }

    /**
     * Start the budget monitoring service
     */
    public void startMonitoring() {
        LoggerUtil.info("Starting budget alert monitoring service");

        // Check budgets every hour
        alertTask = taskManager.scheduleAtFixedRate(
                this::checkBudgetLimits,
                0, // Initial delay
                1, // Period
                TimeUnit.HOURS
        );

        LoggerUtil.info("Budget alert service started - checking every hour");
    }

    /**
     * Stop the budget monitoring service
     */
    public void stopMonitoring() {
        if (alertTask != null && !alertTask.isCancelled()) {
            alertTask.cancel(false);
            LoggerUtil.info("Budget alert service stopped");
        }
    }

    /**
     * Manually trigger a budget check
     */
    public void checkBudgetLimitsNow() {
        taskManager.execute(this::checkBudgetLimits);
    }

    /**
     * Check all category budget limits for the current month
     */
    private void checkBudgetLimits() {
        try {
            LoggerUtil.debug("Checking budget limits for current month");

            YearMonth currentMonth = YearMonth.now();
            LocalDate monthStart = currentMonth.atDay(1);
            LocalDate monthEnd = currentMonth.atEndOfMonth();

            List<Category> categoriesWithBudgets = categoryDAO.findAll().stream()
                    .filter(category -> category.getMonthlyBudgetLimit() != null)
                    .filter(category -> category.getMonthlyBudgetLimit().compareTo(BigDecimal.ZERO) > 0)
                    .toList();

            for (Category category : categoriesWithBudgets) {
                checkCategoryBudget(category, monthStart, monthEnd);
            }

        } catch (Exception e) {
            LoggerUtil.error("Error checking budget limits", e);
        }
    }

    /**
     * Check budget for a specific category
     */
    private void checkCategoryBudget(Category category, LocalDate monthStart, LocalDate monthEnd) {
        try {
            // Get total expenses for this category in the current month
            List<Expense> monthlyExpenses = expenseDAO.findByDateRange(monthStart, monthEnd)
                    .stream()
                    .filter(expense -> expense.getCategory().getId().equals(category.getId()))
                    .toList();

            BigDecimal totalSpent = monthlyExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal budgetLimit = category.getMonthlyBudgetLimit();
            double percentageUsed = totalSpent.divide(budgetLimit, 4, java.math.RoundingMode.HALF_UP).doubleValue();

            LoggerUtil.debug(String.format("Category '%s': $%.2f / $%.2f (%.1f%%)",
                    category.getName(), totalSpent, budgetLimit, percentageUsed * 100));

            // Check thresholds and generate alerts
            if (percentageUsed >= 1.0) {
                generateBudgetExceededAlert(category, totalSpent, budgetLimit, percentageUsed);
            } else if (percentageUsed >= CRITICAL_THRESHOLD) {
                generateCriticalBudgetAlert(category, totalSpent, budgetLimit, percentageUsed);
            } else if (percentageUsed >= WARNING_THRESHOLD) {
                generateWarningBudgetAlert(category, totalSpent, budgetLimit, percentageUsed);
            }

        } catch (Exception e) {
            LoggerUtil.error("Error checking budget for category: " + category.getName(), e);
        }
    }

    /**
     * Generate budget exceeded alert
     */
    private void generateBudgetExceededAlert(Category category, BigDecimal totalSpent, BigDecimal budgetLimit, double percentage) {
        BigDecimal overage = totalSpent.subtract(budgetLimit);
        String message = String.format(
                "BUDGET EXCEEDED: Category '%s' has exceeded its monthly budget by $%.2f (%.1f%% of limit). "
                + "Spent: $%.2f, Budget: $%.2f",
                category.getName(), overage, percentage * 100, totalSpent, budgetLimit
        );

        LoggerUtil.warn(message);
        // In a real application, this could send notifications, emails, etc.
        showBudgetAlert("Budget Exceeded", message, AlertLevel.CRITICAL);
    }

    /**
     * Generate critical budget alert (95%+)
     */
    private void generateCriticalBudgetAlert(Category category, BigDecimal totalSpent, BigDecimal budgetLimit, double percentage) {
        BigDecimal remaining = budgetLimit.subtract(totalSpent);
        String message = String.format(
                "CRITICAL BUDGET WARNING: Category '%s' is at %.1f%% of monthly budget. "
                + "Only $%.2f remaining (Spent: $%.2f, Budget: $%.2f)",
                category.getName(), percentage * 100, remaining, totalSpent, budgetLimit
        );

        LoggerUtil.warn(message);
        showBudgetAlert("Critical Budget Warning", message, AlertLevel.WARNING);
    }

    /**
     * Generate warning budget alert (80%+)
     */
    private void generateWarningBudgetAlert(Category category, BigDecimal totalSpent, BigDecimal budgetLimit, double percentage) {
        BigDecimal remaining = budgetLimit.subtract(totalSpent);
        String message = String.format(
                "Budget Warning: Category '%s' is at %.1f%% of monthly budget. "
                + "$%.2f remaining (Spent: $%.2f, Budget: $%.2f)",
                category.getName(), percentage * 100, remaining, totalSpent, budgetLimit
        );

        LoggerUtil.info(message);
        showBudgetAlert("Budget Warning", message, AlertLevel.INFO);
    }

    /**
     * Show budget alert to user (placeholder for UI integration)
     */
    private void showBudgetAlert(String title, String message, AlertLevel level) {
        // This is a placeholder - in a real application, this would:
        // - Show a popup notification
        // - Send an email
        // - Add to a notifications panel
        // - Play a sound
        // etc.

        // For now, just log at appropriate level
        switch (level) {
            case CRITICAL ->
                LoggerUtil.error("ALERT: " + title + " - " + message);
            case WARNING ->
                LoggerUtil.warn("ALERT: " + title + " - " + message);
            case INFO ->
                LoggerUtil.info("ALERT: " + title + " - " + message);
        }

        // In future, could integrate with Swing to show dialog boxes:
        // SwingUtilities.invokeLater(() -> {
        //     JOptionPane.showMessageDialog(null, message, title, getJOptionPaneType(level));
        // });
    }

    /**
     * Alert severity levels
     */
    public enum AlertLevel {
        INFO, WARNING, CRITICAL
    }

    /**
     * Check if the service is currently monitoring
     */
    public boolean isMonitoring() {
        return alertTask != null && !alertTask.isCancelled();
    }
}
