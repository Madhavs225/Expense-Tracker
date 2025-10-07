package com.expensetracker.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.expensetracker.controller.AppController;
import com.expensetracker.model.Category;

/**
 * Panel for managing categories (add, view, edit)
 */
public class CategoryPanel extends JPanel {

    private final AppController controller;
    private final DefaultListModel<String> listModel;
    private final JList<String> categoryList;
    private final JTextField nameField;
    private final JTextField budgetField;

    public CategoryPanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        // Input form
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Category Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        inputPanel.add(nameField, gbc);

        // Budget Limit (optional)
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Budget Limit:"), gbc);
        gbc.gridx = 1;
        budgetField = new JTextField(15);
        budgetField.setToolTipText("Optional: Leave empty for no budget limit");
        inputPanel.add(budgetField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Category");
        JButton refreshBtn = new JButton("Refresh");
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Category list
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        loadCategories();
        add(new JScrollPane(categoryList), BorderLayout.CENTER);

        // Event handlers
        addBtn.addActionListener(new AddCategoryListener());
        refreshBtn.addActionListener(e -> loadCategories());
    }

    private void loadCategories() {
        listModel.clear();
        try {
            List<Category> categories = controller.getAllCategories();
            for (Category cat : categories) {
                String display = cat.getName();
                if (cat.getMonthlyBudgetLimit() != null) {
                    display += " (Budget: $" + cat.getMonthlyBudgetLimit() + ")";
                }
                listModel.addElement(display);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class AddCategoryListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(CategoryPanel.this, "Please enter a category name",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                BigDecimal budgetLimit = null;
                String budgetText = budgetField.getText().trim();
                if (!budgetText.isEmpty()) {
                    try {
                        budgetLimit = new BigDecimal(budgetText);
                        if (budgetLimit.compareTo(BigDecimal.ZERO) <= 0) {
                            JOptionPane.showMessageDialog(CategoryPanel.this, "Budget limit must be positive",
                                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(CategoryPanel.this, "Invalid budget amount format",
                                "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                Category category = new Category(name);
                category.setMonthlyBudgetLimit(budgetLimit);
                controller.addCategory(category);

                // Clear form
                nameField.setText("");
                budgetField.setText("");

                loadCategories();
                JOptionPane.showMessageDialog(CategoryPanel.this, "Category added successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CategoryPanel.this, "Error adding category: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
