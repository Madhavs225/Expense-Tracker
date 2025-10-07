package com.expensetracker.view;

import javax.swing.*;

/**
 * Application launcher selecting login first.
 */
public class AppLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
