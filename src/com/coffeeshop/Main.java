package com.coffeeshop;

import com.coffeeshop.database.DatabaseManager;
import com.coffeeshop.ui.LoginRegisterWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Could not set Look and Feel: " + ex.getMessage());
            }
        }

        // Initialize Database and Schema
        System.out.println("Initializing Database connection and schema...");
        DatabaseManager.initializeDatabase();

        // Launch application
        System.out.println("Launching Coffee Shop Management System...");
        SwingUtilities.invokeLater(() -> {
            LoginRegisterWindow frame = new LoginRegisterWindow();
            frame.setVisible(true);
        });
    }
}
