package com.expensetracker.report;

import java.time.LocalDate;

/**
 * Request object for generating daily reports
 */
public class DailyReportRequest {

    private final LocalDate date;

    public DailyReportRequest(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
