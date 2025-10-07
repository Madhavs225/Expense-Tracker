package com.expensetracker.view;

import com.expensetracker.dao.impl.UserAccountJdbcDAO;
import com.expensetracker.model.Role;
import com.expensetracker.service.AuthService;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private final AuthService authService;

    public LoginFrame() {
        super("Login - Expense Tracker");
        this.authService = new AuthService(new UserAccountJdbcDAO());
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (authService.login(u, p)) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new MainApp().setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username & password");
                return;
            }
            try {
                authService.register(u, p, Role.USER);
                JOptionPane.showMessageDialog(this, "Registered. Please login.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
