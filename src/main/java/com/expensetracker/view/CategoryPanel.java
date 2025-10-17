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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import com.expensetracker.view.theme.AppTheme;

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
        setLayout(new BorderLayout(15, 15));
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(AppTheme.PANEL_BORDER);

        // Main container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);

        // Header
        JLabel headerLabel = new JLabel("Manage Categories");
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

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setFont(AppTheme.LABEL_FONT);
        nameLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(25);
        AppTheme.styleTextField(nameField);
        inputCard.add(nameField, gbc);

        // Budget Limit (optional)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel budgetLabel = new JLabel("Budget Limit:");
        budgetLabel.setFont(AppTheme.LABEL_FONT);
        budgetLabel.setForeground(AppTheme.TEXT_COLOR);
        inputCard.add(budgetLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        budgetField = new JTextField(25);
        budgetField.setToolTipText("Optional: Leave empty for no budget limit");
        AppTheme.styleTextField(budgetField);
        inputCard.add(budgetField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(AppTheme.CARD_COLOR);
        
        JButton addBtn = new JButton("Add Category");
        AppTheme.styleButton(addBtn, true);
        
        JButton refreshBtn = new JButton("Refresh");
        AppTheme.styleButton(refreshBtn, false);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        inputCard.add(buttonPanel, gbc);

        mainPanel.add(inputCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Category list card
        JPanel listCard = new JPanel(new BorderLayout(10, 10));
        listCard.setBackground(AppTheme.CARD_COLOR);
        listCard.setBorder(AppTheme.CARD_BORDER);
        listCard.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel listLabel = new JLabel("Categories");
        listLabel.setFont(AppTheme.HEADER_FONT);
        listLabel.setForeground(AppTheme.TEXT_COLOR);
        listCard.add(listLabel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        categoryList.setFont(AppTheme.LABEL_FONT);
        categoryList.setBackground(AppTheme.CARD_COLOR);
        categoryList.setForeground(AppTheme.TEXT_COLOR);
        categoryList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        loadCategories();
        
        JScrollPane scrollPane = new JScrollPane(categoryList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(0, 300));
        listCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(listCard);

        add(mainPanel, BorderLayout.CENTER);

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
