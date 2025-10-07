package com.expensetracker.view.panel;

import com.expensetracker.controller.AppController;
import com.expensetracker.model.Category;
import com.expensetracker.model.PaymentMethod;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExpensePanel extends JPanel {

    private final AppController controller;
    private final DefaultTableModel tableModel;

    public ExpensePanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Category", "Amount", "Method", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        refreshRecent();

        JPanel actions = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        actions.add(addBtn);
        actions.add(delBtn);
        actions.add(refreshBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addExpense());
        delBtn.addActionListener(e -> deleteExpense(table));
        refreshBtn.addActionListener(e -> refreshRecent());
    }

    private void refreshRecent() {
        tableModel.setRowCount(0);
        controller.getExpenseService().listRecent(100).forEach(exp
                -> tableModel.addRow(new Object[]{exp.getId(), exp.getDate(), exp.getCategory().getName(), exp.getAmount(), exp.getPaymentMethod(), exp.getDescription()})
        );
    }

    private void addExpense() {
        java.util.List<Category> categories = controller.getCategoryService().listCategories();
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Create a category first");
            return;
        }
        JComboBox<Category> categoryCombo = new JComboBox<>(categories.toArray(new Category[0]));
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField amountField = new JTextField();
        JComboBox<PaymentMethod> methodCombo = new JComboBox<>(PaymentMethod.values());
        JTextField descField = new JTextField();
        Object[] msg = {"Category", categoryCombo, "Date (YYYY-MM-DD)", dateField, "Amount", amountField, "Method", methodCombo, "Description", descField};
        if (JOptionPane.showConfirmDialog(this, msg, "Add Expense", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Category cat = (Category) categoryCombo.getSelectedItem();
                LocalDate date = LocalDate.parse(dateField.getText());
                BigDecimal amount = new BigDecimal(amountField.getText());
                PaymentMethod method = (PaymentMethod) methodCombo.getSelectedItem();
                controller.getExpenseService().addExpense(cat, date, amount, method, descField.getText());
                refreshRecent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        }
    }

    private void deleteExpense(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete expense?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.getExpenseService().deleteExpense(id);
            refreshRecent();
        }
    }
}
