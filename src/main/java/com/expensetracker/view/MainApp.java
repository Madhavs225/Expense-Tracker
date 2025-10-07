package com.expensetracker.view;

import com.expensetracker.controller.AppController;
import com.expensetracker.report.CsvExportStrategy;
import com.expensetracker.report.DailyReport;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import java.time.LocalDate;
import java.io.FileWriter;

import java.io.IOException;

/**
 * Initial Swing entry point (temporary basic frame) - will evolve.
 */
public class MainApp extends JFrame {

    private final AppController controller;

    public MainApp() {
        super("Expense Tracker");
        this.controller = new AppController();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", new JLabel("Dashboard (to implement)"));
        tabs.addTab("Expenses", new ExpensePanel(controller));
        tabs.addTab("Categories", new CategoryPanel(controller));
        tabs.addTab("Reports", buildReportsPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JTextField dateField = new JTextField(LocalDate.now().toString(), 12);
        JButton genBtn = new JButton("Generate Daily Report");
        top.add(new JLabel("Date:"));
        top.add(dateField);
        top.add(genBtn);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        genBtn.addActionListener(e -> {
            try {
                LocalDate d = LocalDate.parse(dateField.getText().trim());
                DailyReport report = controller.generateDailyReport(d);
                StringBuilder sb = new StringBuilder();
                sb.append(report.getTitle()).append("\nTotal: ").append(report.getTotal())
                        .append(" Count: ").append(report.getCount()).append("\n");
                report.getLines().forEach(l -> sb.append(l).append('\n'));
                area.setText(sb.toString());
                // auto export to reports folder
                java.nio.file.Path dir = java.nio.file.Paths.get("reports");
                java.nio.file.Files.createDirectories(dir);
                java.nio.file.Path file = dir.resolve("daily-" + d + ".csv");
                try (FileWriter fw = new FileWriter(file.toFile())) {
                    new CsvExportStrategy().export(report, fw);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Report failed: " + ex.getMessage());
            }
        });
        return panel;
    }

}
