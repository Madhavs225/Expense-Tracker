package com.expensetracker.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.FileWriter;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.expensetracker.controller.AppController;
import com.expensetracker.report.CsvExportStrategy;
import com.expensetracker.report.DailyReport;
import com.expensetracker.util.SessionContext;
import com.expensetracker.view.theme.AppTheme;

/**
 * Main application window with tabbed interface for expense management.
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
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Set application background
        getContentPane().setBackground(AppTheme.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create header with user info and logout
        JPanel headerPanel = createHeaderPanel();

        // Create main content with tabs
        JTabbedPane tabs = createStyledTabbedPane();
        tabs.addTab("📊 Dashboard", createDashboardPanel());
        tabs.addTab("💰 Expenses", new ExpensePanel(controller));
        tabs.addTab("📁 Categories", new CategoryPanel(controller));
        tabs.addTab("📈 Reports", createReportsPanel());

        // Layout
        add(headerPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // App title
        JLabel titleLabel = new JLabel("Expense Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(AppTheme.PRIMARY_COLOR);

        String currentUser = SessionContext.getInstance().getCurrentUser() != null
                ? SessionContext.getInstance().getCurrentUser().getUsername() : "User";
        JLabel userLabel = new JLabel("Welcome, " + currentUser);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(AppTheme.LABEL_FONT);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(AppTheme.PRIMARY_COLOR);
        logoutBtn.setFont(AppTheme.BUTTON_FONT);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            SessionContext.getInstance().clear();
            dispose();
            new LoginFrame().setVisible(true);
        });

        rightPanel.add(userLabel);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.LABEL_FONT);
        tabs.setBackground(AppTheme.BACKGROUND_COLOR);
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return tabs;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(AppTheme.BACKGROUND_COLOR);
        dashboard.setBorder(AppTheme.PANEL_BORDER);

        // Welcome section
        JPanel welcomeCard = AppTheme.createTitledCard("Dashboard Overview");
        welcomeCard.setLayout(new GridLayout(2, 2, 15, 15));

        // Quick stats cards (placeholder for now)
        welcomeCard.add(createStatCard("Total Expenses", "$0.00", AppTheme.ACCENT_COLOR));
        welcomeCard.add(createStatCard("This Month", "$0.00", AppTheme.PRIMARY_COLOR));
        welcomeCard.add(createStatCard("Categories", "0", AppTheme.SUCCESS_COLOR));
        welcomeCard.add(createStatCard("Last Transaction", "None", AppTheme.WARNING_COLOR));

        dashboard.add(welcomeCard, BorderLayout.NORTH);

        // Quick actions
        JPanel actionsCard = AppTheme.createTitledCard("Quick Actions");
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addExpenseBtn = new JButton("+ Add Expense");
        AppTheme.styleButton(addExpenseBtn, true);
        addExpenseBtn.addActionListener(e -> {
            // Switch to expenses tab
            ((JTabbedPane) getContentPane().getComponent(1)).setSelectedIndex(1);
        });

        JButton addCategoryBtn = new JButton("+ Add Category");
        AppTheme.styleButton(addCategoryBtn, false);
        addCategoryBtn.addActionListener(e -> {
            // Switch to categories tab
            ((JTabbedPane) getContentPane().getComponent(1)).setSelectedIndex(2);
        });

        JButton generateReportBtn = new JButton("📈 Generate Report");
        AppTheme.styleButton(generateReportBtn, false);
        generateReportBtn.addActionListener(e -> {
            // Switch to reports tab
            ((JTabbedPane) getContentPane().getComponent(1)).setSelectedIndex(3);
        });

        actionsCard.add(addExpenseBtn);
        actionsCard.add(addCategoryBtn);
        actionsCard.add(generateReportBtn);

        dashboard.add(actionsCard, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.LABEL_FONT);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createReportsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(AppTheme.PANEL_BORDER);

        // Report generation card
        JPanel genCard = AppTheme.createTitledCard("Generate Daily Report");
        genCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Date selection
        JLabel dateLabel = new JLabel("Select Date:");
        AppTheme.styleLabel(dateLabel, false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        genCard.add(dateLabel, gbc);

        JTextField dateField = new JTextField(LocalDate.now().toString(), 15);
        AppTheme.styleTextField(dateField);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        genCard.add(dateField, gbc);

        JButton genBtn = new JButton("Generate Report");
        AppTheme.styleButton(genBtn, true);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        genCard.add(genBtn, gbc);

        // Results area
        JPanel resultsCard = AppTheme.createTitledCard("Report Results");
        resultsCard.setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(AppTheme.BACKGROUND_COLOR);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(null);
        resultsCard.add(scrollPane, BorderLayout.CENTER);

        // Event handler for report generation
        genBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                DailyReport report = controller.generateDailyReport(date);

                StringBuilder sb = new StringBuilder();
                sb.append(report.getTitle()).append("\n");
                sb.append("=".repeat(50)).append("\n");
                sb.append(String.format("Total Amount: $%.2f\n", report.getTotal()));
                sb.append(String.format("Number of Expenses: %d\n", report.getCount()));
                sb.append("\nExpense Details:\n");
                sb.append("-".repeat(50)).append("\n");

                if (report.getLines().isEmpty()) {
                    sb.append("No expenses found for this date.\n");
                } else {
                    report.getLines().forEach(line -> sb.append(line).append('\n'));
                }

                area.setText(sb.toString());

                // Auto export to CSV
                java.nio.file.Path dir = java.nio.file.Paths.get("reports");
                java.nio.file.Files.createDirectories(dir);
                java.nio.file.Path file = dir.resolve("daily-" + date + ".csv");

                try (FileWriter fw = new FileWriter(file.toFile())) {
                    new CsvExportStrategy().export(report, fw);
                }

                AppTheme.showSuccessMessage(this,
                        "Report generated successfully!\nCSV file saved to: " + file.toString());

            } catch (Exception ex) {
                AppTheme.showErrorMessage(this, "Report generation failed: " + ex.getMessage());
            }
        });

        // Layout
        mainPanel.add(genCard, BorderLayout.NORTH);
        mainPanel.add(resultsCard, BorderLayout.CENTER);

        return mainPanel;
    }
}
