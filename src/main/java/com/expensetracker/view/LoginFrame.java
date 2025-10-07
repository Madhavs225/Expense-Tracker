package com.expensetracker.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
        setSize(520, 400);
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
        JLabel titleLabel = new JLabel("Expense Tracker", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.PRIMARY_COLOR);
        JLabel subtitleLabel = new JLabel("Please sign in to continue", SwingConstants.CENTER);
        AppTheme.styleLabel(subtitleLabel, false);

        JPanel headerContainer = new JPanel();
        headerContainer.setLayout(new BoxLayout(headerContainer, BoxLayout.Y_AXIS));
        headerContainer.setOpaque(false);
        headerContainer.add(titleLabel);
        headerContainer.add(Box.createVerticalStrut(6));
        headerContainer.add(subtitleLabel);
        headerContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Login form card
        JPanel formCard = AppTheme.createCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Username field
        JLabel userLabel = new JLabel("Username");
        AppTheme.styleLabel(userLabel, false);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formCard.add(userLabel, gbc);
        row++;

        JTextField userField = new JTextField();
        AppTheme.styleTextField(userField);
        userField.setColumns(22);
        gbc.gridy = row;
        formCard.add(userField, gbc);
        row++;

        // Password field
        JLabel passLabel = new JLabel("Password");
        AppTheme.styleLabel(passLabel, false);
        gbc.gridy = row;
        formCard.add(passLabel, gbc);
        row++;

        JPasswordField passField = new JPasswordField();
        passField.setColumns(22);
        AppTheme.styleTextField(passField);
        gbc.gridy = row;
        formCard.add(passField, gbc);
        row++;

        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setFont(AppTheme.LABEL_FONT);
        showPassword.setForeground(AppTheme.TEXT_SECONDARY);
        showPassword.setOpaque(false);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passField.setEchoChar('\0');
            } else {
                passField.setEchoChar((Character) javax.swing.UIManager.getDefaults().get("PasswordField.echoChar"));
            }
        });
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formCard.add(showPassword, gbc);
        row++;

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

        gbc.gridy = row;
        gbc.gridwidth = 2;
        formCard.add(buttonPanel, gbc);
        row++;

        // Keyboard shortcuts
        // ESC to close
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

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
