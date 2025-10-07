package com.expensetracker.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Monthly expense report implementation
 */
public class MonthlyReport extends AbstractReport {

    private final int year;
    private final int month;
    private final BigDecimal total;
    private final int count;
    private final Map<String, BigDecimal> categoryTotals;
    private final Map<Integer, BigDecimal> weeklyTotals; // week number -> total

    public MonthlyReport(int year, int month, BigDecimal total, int count,
            Map<String, BigDecimal> categoryTotals, Map<Integer, BigDecimal> weeklyTotals,
            List<String> lines) {
        super(generateTitle(year, month), lines);
        this.year = year;
        this.month = month;
        this.total = total;
        this.count = count;
        this.categoryTotals = categoryTotals;
        this.weeklyTotals = weeklyTotals;
    }

    private static String generateTitle(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return String.format("Monthly Report - %s", date.format(formatter));
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
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

    public Map<Integer, BigDecimal> getWeeklyTotals() {
        return weeklyTotals;
    }

    public BigDecimal getDailyAverage() {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        int daysInMonth = monthStart.lengthOfMonth();
        return total.divide(BigDecimal.valueOf(daysInMonth), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getWeeklyAverage() {
        if (weeklyTotals.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(weeklyTotals.size()), 2, java.math.RoundingMode.HALF_UP);
    }
}
