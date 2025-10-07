package com.expensetracker.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Weekly expense report implementation
 */
public class WeeklyReport extends AbstractReport {

    private final LocalDate weekStartDate;
    private final LocalDate weekEndDate;
    private final BigDecimal total;
    private final int count;
    private final Map<String, BigDecimal> categoryTotals;
    private final Map<LocalDate, BigDecimal> dailyTotals;

    public WeeklyReport(LocalDate weekStartDate, LocalDate weekEndDate, BigDecimal total, int count,
            Map<String, BigDecimal> categoryTotals, Map<LocalDate, BigDecimal> dailyTotals,
            List<String> lines) {
        super(generateTitle(weekStartDate, weekEndDate), lines);
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.total = total;
        this.count = count;
        this.categoryTotals = categoryTotals;
        this.dailyTotals = dailyTotals;
    }

    private static String generateTitle(LocalDate weekStartDate, LocalDate weekEndDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return String.format("Weekly Report (%s - %s)",
                weekStartDate.format(formatter),
                weekEndDate.format(formatter));
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public int getCount() {
        return count;
    }

    public Map<String, BigDecimal> getCategoryTotals() {
        return categoryTotals;
    }

    public Map<LocalDate, BigDecimal> getDailyTotals() {
        return dailyTotals;
    }

    public BigDecimal getDailyAverage() {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(weekStartDate, weekEndDate) + 1;
        return total.divide(BigDecimal.valueOf(daysBetween), 2, java.math.RoundingMode.HALF_UP);
    }
}
