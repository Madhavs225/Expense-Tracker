package com.expensetracker.view.panel;

import com.expensetracker.controller.AppController;
import com.expensetracker.model.Category;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Swing panel for managing categories.
 */
public class CategoryPanel extends JPanel {

    private final AppController controller;
    private final DefaultTableModel tableModel;

    public CategoryPanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Monthly Limit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        refresh();

        JPanel actions = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(delBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addCategory());
        editBtn.addActionListener(e -> editCategory(table));
        delBtn.addActionListener(e -> deleteCategory(table));
    }

    private void refresh() {
        tableModel.setRowCount(0);
        controller.getCategoryService().listCategories().forEach(c
                -> tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getMonthlyBudgetLimit()})
        );
    }

    private void addCategory() {
        JTextField nameField = new JTextField();
        JTextField limitField = new JTextField();
        Object[] msg = {"Name", nameField, "Monthly Limit (optional)", limitField};
        if (JOptionPane.showConfirmDialog(this, msg, "Add Category", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                BigDecimal limit = limitField.getText().isBlank() ? null : new BigDecimal(limitField.getText());
                controller.getCategoryService().createCategory(nameField.getText(), limit);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        }
    }

    private void editCategory(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row");
            return;
        }
        Integer id = (Integer) table.getValueAt(row, 0);
        String currentName = (String) table.getValueAt(row, 1);
        Object currentLimitObj = table.getValueAt(row, 2);
        JTextField nameField = new JTextField(currentName);
        JTextField limitField = new JTextField(currentLimitObj == null ? "" : currentLimitObj.toString());
        Object[] msg = {"Name", nameField, "Monthly Limit", limitField};
        if (JOptionPane.showConfirmDialog(this, msg, "Edit Category", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                BigDecimal limit = limitField.getText().isBlank() ? null : new BigDecimal(limitField.getText());
                Category cat = controller.getCategoryService().findByName(currentName).orElseThrow();
                cat.setName(nameField.getText());
                cat.setMonthlyBudgetLimit(limit);
                controller.getCategoryService().updateCategory(cat);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        }
    }

    private void deleteCategory(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row");
            return;
        }
        Integer id = (Integer) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete category?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.getCategoryService().deleteCategory(id);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        }
    }
}
