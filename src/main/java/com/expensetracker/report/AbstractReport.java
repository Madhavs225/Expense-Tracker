package com.expensetracker.report;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Base report with metadata and lines of content.
 */
public abstract class AbstractReport {

    private final LocalDateTime generatedAt = LocalDateTime.now();
    private final String title;
    private final List<String> lines;

    protected AbstractReport(String title, List<String> lines) {
        this.title = title;
        this.lines = List.copyOf(lines);
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
