package com.expensetracker.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.expensetracker.dao.impl.UserAccountJdbcDAO;
import com.expensetracker.model.Role;
import com.expensetracker.service.AuthService;
import com.expensetracker.view.theme.AppTheme;

public class LoginFrame extends JFrame {

    private final AuthService authService;

    public LoginFrame() {
        super("Expense Tracker - Login");
        this.authService = new AuthService(new UserAccountJdbcDAO());
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set the background
        getContentPane().setBackground(AppTheme.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header panel with title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Expense Tracker", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.PRIMARY_COLOR);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Please sign in to continue", SwingConstants.CENTER);
        AppTheme.styleLabel(subtitleLabel, false);

        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(AppTheme.BACKGROUND_COLOR);
        headerContainer.add(titleLabel, BorderLayout.CENTER);
        headerContainer.add(subtitleLabel, BorderLayout.SOUTH);
        headerContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Login form card
        JPanel formCard = AppTheme.createCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JLabel userLabel = new JLabel("Username:");
        AppTheme.styleLabel(userLabel, false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formCard.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        AppTheme.styleTextField(userField);
        gbc.gridy = 1;
        formCard.add(userField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        AppTheme.styleLabel(passLabel, false);
        gbc.gridy = 2;
        formCard.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        AppTheme.styleTextField(passField);
        gbc.gridy = 3;
        formCard.add(passField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        buttonPanel.setBackground(AppTheme.CARD_COLOR);

        JButton loginBtn = new JButton("Sign In");
        AppTheme.styleButton(loginBtn, true);
        loginBtn.setPreferredSize(new Dimension(100, AppTheme.BUTTON_HEIGHT));

        JButton registerBtn = new JButton("Register");
        AppTheme.styleButton(registerBtn, false);
        registerBtn.setPreferredSize(new Dimension(100, AppTheme.BUTTON_HEIGHT));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formCard.add(buttonPanel, gbc);

        // Event handlers
        loginBtn.addActionListener(e -> handleLogin(userField, passField));
        registerBtn.addActionListener(e -> handleRegister(userField, passField));

        // Enter key support
        getRootPane().setDefaultButton(loginBtn);
        passField.addActionListener(e -> handleLogin(userField, passField));

        // Add components to main panel
        mainPanel.add(headerContainer, BorderLayout.NORTH);
        mainPanel.add(formCard, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleLogin(JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            AppTheme.showWarningMessage(this, "Please enter both username and password.");
            return;
        }

        try {
            if (authService.login(username, password)) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new MainApp().setVisible(true);
                });
            } else {
                AppTheme.showErrorMessage(this, "Invalid username or password.");
                passField.setText("");
                passField.requestFocus();
            }
        } catch (Exception ex) {
            AppTheme.showErrorMessage(this, "Login failed: " + ex.getMessage());
        }
    }

    private void handleRegister(JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            AppTheme.showWarningMessage(this, "Please enter both username and password.");
            return;
        }

        if (password.length() < 4) {
            AppTheme.showWarningMessage(this, "Password must be at least 4 characters long.");
            return;
        }

        try {
            authService.register(username, password, Role.USER);
            AppTheme.showSuccessMessage(this, "Registration successful! Please sign in with your new account.");
            passField.setText("");
            userField.requestFocus();
        } catch (Exception ex) {
            AppTheme.showErrorMessage(this, "Registration failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
