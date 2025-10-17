package com.expensetracker.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.expensetracker.controller.AppController;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.view.theme.AppTheme;

/**
 * Panel for managing expenses (add, view, edit)
 */
public class ExpensePanel extends JPanel {

    private final AppController controller;
    private final DefaultListModel<String> listModel;
    private final JList<String> expenseList;
    private final JTextField dateField;
    private final JTextField amountField;
    private final JComboBox<Category> categoryCombo;
    private final JComboBox<PaymentMethod> paymentCombo;
    private final JTextField descriptionField;

    public ExpensePanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(15, 15));
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(AppTheme.PANEL_BORDER);

        // Main container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);

        // Header
        JLabel headerLabel = new JLabel("Manage Expenses");
        headerLabel.setFont(AppTheme.HEADER_FONT);
        headerLabel.setForeground(AppTheme.TEXT_COLOR);
        headerLabel.setAlignmentX(LEFT_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Input form card
        JPanel inputCard = new JPanel(new GridBagLayout());
        inputCard.setBackground(AppTheme.CARD_COLOR);
        inputCard.setBorder(AppTheme.CARD_BORDER);
        inputCard.setAlignmentX(LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(AppTheme.LABEL_FONT);
        dateLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dateField = new JTextField(LocalDate.now().toString(), 20);
        AppTheme.styleTextField(dateField);
        inputCard.add(dateField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(AppTheme.LABEL_FONT);
        amountLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        amountField = new JTextField(20);
        AppTheme.styleTextField(amountField);
        inputCard.add(amountField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(AppTheme.LABEL_FONT);
        categoryLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        categoryCombo = new JComboBox<>();
        AppTheme.styleComboBox(categoryCombo);
        loadCategories();
        inputCard.add(categoryCombo, gbc);

        // Payment Method
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setFont(AppTheme.LABEL_FONT);
        paymentLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(paymentLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        paymentCombo = new JComboBox<>(PaymentMethod.values());
        AppTheme.styleComboBox(paymentCombo);
        inputCard.add(paymentCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(AppTheme.LABEL_FONT);
        descLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(descLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionField = new JTextField(20);
        AppTheme.styleTextField(descriptionField);
        inputCard.add(descriptionField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(AppTheme.CARD_COLOR);
        
        JButton addBtn = new JButton("Add Expense");
        AppTheme.styleButton(addBtn, true);
        
        JButton refreshBtn = new JButton("Refresh");
        AppTheme.styleButton(refreshBtn, false);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        inputCard.add(buttonPanel, gbc);

        mainPanel.add(inputCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Expense list card
        JPanel listCard = new JPanel(new BorderLayout(10, 10));
        listCard.setBackground(AppTheme.CARD_COLOR);
        listCard.setBorder(AppTheme.CARD_BORDER);
        listCard.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel listLabel = new JLabel("Recent Expenses");
        listLabel.setFont(AppTheme.HEADER_FONT);
        listLabel.setForeground(AppTheme.TEXT_COLOR);
        listCard.add(listLabel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        expenseList = new JList<>(listModel);
        expenseList.setFont(AppTheme.LABEL_FONT);
        expenseList.setBackground(AppTheme.CARD_COLOR);
        expenseList.setForeground(AppTheme.TEXT_COLOR);
        expenseList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        loadExpenses();
        
        JScrollPane scrollPane = new JScrollPane(expenseList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(0, 300));
        listCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(listCard);

        add(mainPanel, BorderLayout.CENTER);

        // Event handlers
        addBtn.addActionListener(new AddExpenseListener());
        refreshBtn.addActionListener(e -> {
            loadCategories();
            loadExpenses();
        });
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        try {
            List<Category> categories = controller.getAllCategories();
            for (Category cat : categories) {
                categoryCombo.addItem(cat);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExpenses() {
        listModel.clear();
        try {
            List<Expense> expenses = controller.getAllExpenses();
            for (Expense exp : expenses) {
                String display = String.format("%s - $%.2f - %s (%s)",
                        exp.getDate(), exp.getAmount(), exp.getDescription(), exp.getCategory().getName());
                listModel.addElement(display);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class AddExpenseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                Category category = (Category) categoryCombo.getSelectedItem();
                PaymentMethod payment = (PaymentMethod) paymentCombo.getSelectedItem();
                String description = descriptionField.getText().trim();

                if (category == null) {
                    JOptionPane.showMessageDialog(ExpensePanel.this, "Please select a category",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Expense expense = Expense.create(category, date, amount, payment, description);
                controller.addExpense(expense);

                // Clear form
                amountField.setText("");
                descriptionField.setText("");
                dateField.setText(LocalDate.now().toString());

                loadExpenses();
                JOptionPane.showMessageDialog(ExpensePanel.this, "Expense added successfully!");

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(ExpensePanel.this, "Invalid date format. Use YYYY-MM-DD",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ExpensePanel.this, "Invalid amount format",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ExpensePanel.this, "Error adding expense: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
