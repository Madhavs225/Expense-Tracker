package com.expensetracker.report;

import java.time.LocalDate;

/**
 * Request object for generating weekly reports
 */
public class WeeklyReportRequest {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public WeeklyReportRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public WeeklyReportRequest(LocalDate weekOfDate) {
        // Calculate week start (Monday) and end (Sunday)
        this.startDate = weekOfDate.with(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).dayOfWeek(), 1);
        this.endDate = this.startDate.plusDays(6);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
