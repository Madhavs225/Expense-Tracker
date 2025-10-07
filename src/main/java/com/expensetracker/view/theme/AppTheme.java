package com.expensetracker.view.theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Professional UI theme and styling constants for the Expense Tracker
 * application. Provides consistent colors, fonts, borders, and styling
 * throughout the app.
 */
public class AppTheme {

    // Color palette - Modern blue/gray theme
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);        // Professional blue
    public static final Color PRIMARY_DARK = new Color(31, 97, 141);          // Darker blue for hover
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);          // Red for delete/error
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);         // Green for success
    public static final Color WARNING_COLOR = new Color(243, 156, 18);        // Orange for warnings

    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);    // Light gray background
    public static final Color CARD_COLOR = Color.WHITE;                       // White cards
    public static final Color BORDER_COLOR = new Color(220, 221, 222);        // Light border
    public static final Color TEXT_COLOR = new Color(52, 58, 64);             // Dark gray text
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);      // Lighter gray text

    // Fonts
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    // Dimensions
    public static final int BUTTON_HEIGHT = 35;
    public static final int INPUT_HEIGHT = 30;
    public static final int CARD_PADDING = 20;
    public static final int COMPONENT_SPACING = 10;
    public static final int BORDER_RADIUS = 5;

    // Borders
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
    );

    public static final Border INPUT_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
    );

    public static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15);

    /**
     * Applies primary button styling
     */
    public static void styleButton(JButton button, boolean isPrimary) {
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, BUTTON_HEIGHT));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(TEXT_COLOR);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            button.setBorderPainted(true);
        }

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(PRIMARY_DARK);
                } else {
                    button.setBackground(BACKGROUND_COLOR);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(PRIMARY_COLOR);
                } else {
                    button.setBackground(Color.WHITE);
                }
            }
        });
    }

    /**
     * Applies styling to input fields
     */
    public static void styleTextField(JTextField field) {
        field.setFont(INPUT_FONT);
        field.setBorder(INPUT_BORDER);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, INPUT_HEIGHT));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
    }

    /**
     * Applies styling to combo boxes
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(INPUT_FONT);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, INPUT_HEIGHT));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
    }

    /**
     * Applies styling to labels
     */
    public static void styleLabel(JLabel label, boolean isHeader) {
        if (isHeader) {
            label.setFont(HEADER_FONT);
            label.setForeground(TEXT_COLOR);
        } else {
            label.setFont(LABEL_FONT);
            label.setForeground(TEXT_SECONDARY);
        }
    }

    /**
     * Creates a styled panel with card appearance
     */
    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(CARD_BORDER);
        return panel;
    }

    /**
     * Creates a titled card panel
     */
    public static JPanel createTitledCard(String title) {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title);
        styleLabel(titleLabel, true);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    /**
     * Shows a styled success message
     */
    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a styled error message
     */
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a styled warning message
     */
    public static void showWarningMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning",
                JOptionPane.WARNING_MESSAGE);
    }
}
