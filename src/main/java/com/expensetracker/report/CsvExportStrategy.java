package com.expensetracker.report;

import java.io.IOException;
import java.io.Writer;

/**
 * Simple CSV exporter that writes header + lines from report.
 */
public class CsvExportStrategy {

    public void export(AbstractReport report, Writer writer) throws IOException {
        writer.write("# " + report.getTitle() + "\n");
        writer.write("# Generated At: " + report.getGeneratedAt() + "\n");
        for (String line : report.getLines()) {
            writer.write(line + "\n");
        }
    }
}
