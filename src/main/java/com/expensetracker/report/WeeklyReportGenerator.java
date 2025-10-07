package com.expensetracker.report;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.utilnArrayListdao.ExpenseDAO;
import java.util.List;
import java.util.Map;
import com.expensetracker.model.Expense;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;
import com.expensetracker.util.FileManager;
import com.expensetracker.util.LoggerUtil;

/**
 * Generates weekly expense reports
 */
public class WeeklyReportGenerator implements ReportGenerator<WeeklyReport, WeeklyReportRequest> {

    private final ExpenseDAO expenseDAO;

    public WeeklyReportGenerator(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    @Override
    public WeeklyReport generate(WeeklyReportRequest request) {
        LoggerUtil.info("Generating weekly report from " + request.getStartDate() + " to " + request.getEndDate());

        try {
            LocalDate weekStart = request.getStartDate();
            LocalDate weekEnd = request.getEndDate();

            List<Expense> expenses = expenseDAO.findByDateRange(weekStart, weekEnd);

            // Calculate totals
            BigDecimal total = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int count = expenses.size();

            // Group by category
            Map<String, BigDecimal> categoryTotals = expenses.stream()
                    .collect(Collectors.groupingBy(
                            expense -> expense.getCategory().getName(),
                            Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                    ));

            // Group by day
            Map<LocalDate, BigDecimal> dailyTotals = expenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getDate,
                            Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                    ));

            // Generate report lines
            List<String> lines = generateReportLines(weekStart, weekEnd, expenses, total, count, categoryTotals, dailyTotals);

            WeeklyReport report = new WeeklyReport(weekStart, weekEnd, total, count, categoryTotals, dailyTotals, lines);

            LoggerUtil.info("Weekly report generated successfully with " + count + " expenses totaling $" + total);
            return report;

        } catch (Exception e) {
            LoggerUtil.error("Failed to generate weekly report", e);
            throw new RuntimeException("Failed to generate weekly report", e);
        }
    }

    /**
     * Export report using the specified strategy
     */
    public void exportReport(WeeklyReport report, ExportStrategy exportStrategy) {
        try {
            String filename = FileManager.getInstance().createTimestampedFilename("weekly_report", exportStrategy.getFileExtension());
            Path filePath = FileManager.getInstance().createReportPath(filename);
            exportStrategy.exportToFile(report, filePath);
            LoggerUtil.info("Weekly report exported to: " + filePath);
        } catch (Exception e) {
            LoggerUtil.error("Failed to export weekly report", e);
            throw new RuntimeException("Failed to export weekly report", e);
        }
    }

    private List<String> generateReportLines(LocalDate weekStart, LocalDate weekEnd, List<Expense> expenses,
            BigDecimal total, int count, Map<String, BigDecimal> categoryTotals,
            Map<LocalDate, BigDecimal> dailyTotals) {
        List<String> lines = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        // Header
        lines.add("Weekly Expense Report");
        lines.add("Report Period: " + weekStart.format(formatter) + " to " + weekEnd.format(formatter));
        lines.add("");

        // Summary
        lines.add("SUMMARY");
        lines.add("=======");
        lines.add("Total Expenses: $" + String.format("%.2f", total));
        lines.add("Total Transactions: " + count);
        lines.add("Daily Average: $" + String.format("%.2f", total.divide(BigDecimal.valueOf(7), 2, java.math.RoundingMode.HALF_UP)));
        lines.add("");

        // Category breakdown
        lines.add("BY CATEGORY");
        lines.add("===========");
        categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = total.compareTo(BigDecimal.ZERO) > 0
                            ? entry.getValue().divide(total, 4, java.math.RoundingMode.HALF_UP).doubleValue() * 100 : 0;
                    lines.add(String.format("%-20s $%8.2f (%5.1f%%)",
                            entry.getKey(), entry.getValue(), percentage));
                });
        lines.add("");

        // Daily breakdown
        lines.add("DAILY BREAKDOWN");
        lines.add("===============");
        LocalDate currentDate = weekStart;
        while (!currentDate.isAfter(weekEnd)) {
            BigDecimal dayTotal = dailyTotals.getOrDefault(currentDate, BigDecimal.ZERO);
            lines.add(String.format("%-15s $%8.2f",
                    currentDate.format(DateTimeFormatter.ofPattern("EEE MMM dd")), dayTotal));
            currentDate = currentDate.plusDays(1);
        }
        lines.add("");

        // Detailed transactions
        if (!expenses.isEmpty()) {
            lines.add("DETAILED TRANSACTIONS");
            lines.add("====================");
            Map<LocalDate, List<Expense>> expensesByDate = expenses.stream()
                    .collect(Collectors.groupingBy(Expense::getDate));

            currentDate = weekStart;
            while (!currentDate.isAfter(weekEnd)) {
                List<Expense> dayExpenses = expensesByDate.get(currentDate);
                if (dayExpenses != null && !dayExpenses.isEmpty()) {
                    lines.add("");
                    lines.add(currentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
                    lines.add("-".repeat(40));

                    dayExpenses.stream()
                            .sorted(Comparator.comparing(Expense::getAmount).reversed())
                            .forEach(expense -> {
                                lines.add(String.format("  %-20s %-12s $%8.2f %s",
                                        expense.getCategory().getName(),
                                        expense.getPaymentMethod(),
                                        expense.getAmount(),
                                        expense.getDescription() != null ? expense.getDescription() : ""));
                            });
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        return lines;
    }
}
