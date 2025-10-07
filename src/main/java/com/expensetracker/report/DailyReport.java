package com.expensetracker.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Concrete daily expense report with totals and per-category breakdown.
 */
public class DailyReport extends AbstractReport {

    private final LocalDate date;
    private final BigDecimal total;
    private final int count;
    private final Map<String, BigDecimal> categoryTotals;

    public DailyReport(LocalDate date, BigDecimal total, int count, Map<String, BigDecimal> categoryTotals, List<String> lines) {
        super("Daily Report - " + date, lines);
        this.date = date;
        this.total = total;
        this.count = count;
        this.categoryTotals = categoryTotals;
    }

    public LocalDate getDate() {
        return date;
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
}
