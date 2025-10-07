package com.expensetracker.report;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple CSV exporter that writes header + lines from report.
 */
public class CsvExportStrategy implements ExportStrategy {

    public void export(AbstractReport report, Writer writer) throws IOException {
        writer.write("# " + report.getTitle() + "\n");
        writer.write("# Generated At: " + report.getGeneratedAt() + "\n");
        for (String line : report.getLines()) {
            writer.write(line + "\n");
        }
    }

    @Override
    public void exportToFile(AbstractReport report, Path filePath) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            export(report, writer);
        }
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }
}
