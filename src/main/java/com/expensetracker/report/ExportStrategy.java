package com.expensetracker.report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Strategy interface for exporting reports to different formats
 */
public interface ExportStrategy {

    /**
     * Export a report to a file
     *
     * @param report The report to export
     * @param filePath The path where the report should be saved
     * @throws IOException if export fails
     */
    void exportToFile(AbstractReport report, Path filePath) throws IOException;

    /**
     * Get the file extension for this export strategy
     *
     * @return file extension (e.g., "csv", "txt", "pdf")
     */
    String getFileExtension();
}
