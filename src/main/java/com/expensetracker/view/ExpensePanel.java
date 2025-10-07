package com.expensetracker.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

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
        setLayout(new BorderLayout());

        // Input form
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(LocalDate.now().toString(), 12);
        inputPanel.add(dateField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(12);
        inputPanel.add(amountField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        loadCategories();
        inputPanel.add(categoryCombo, gbc);

        // Payment Method
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Payment:"), gbc);
        gbc.gridx = 1;
        paymentCombo = new JComboBox<>(PaymentMethod.values());
        inputPanel.add(paymentCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        inputPanel.add(descriptionField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Expense");
        JButton refreshBtn = new JButton("Refresh");
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Expense list
        listModel = new DefaultListModel<>();
        expenseList = new JList<>(listModel);
        loadExpenses();
        add(new JScrollPane(expenseList), BorderLayout.CENTER);

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
