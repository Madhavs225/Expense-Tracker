package com.expensetracker.report;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates a DailyReport for a single date.
 */
public class DailyReportGenerator implements ReportGenerator<DailyReport, LocalDate> {

    private final ExpenseDAO expenseDAO;

    public DailyReportGenerator(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    @Override
    public DailyReport generate(LocalDate date) {
        List<Expense> expenses = expenseDAO.findByDateRange(date, date);
        BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> perCategory = new LinkedHashMap<>();
        expenses.forEach(e -> perCategory.merge(e.getCategory().getName(), e.getAmount(), BigDecimal::add));
        List<String> lines = expenses.stream()
                .map(e -> e.getDate() + "," + e.getCategory().getName() + "," + e.getAmount() + "," + e.getPaymentMethod() + "," + (e.getDescription() == null ? "" : e.getDescription().replace(",", " ")))
                .collect(Collectors.toList());
        return new DailyReport(date, total, expenses.size(), perCategory, lines);
    }
}
