package com.coffeeshop.ui;

import com.coffeeshop.database.DatabaseManager;
import com.coffeeshop.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

public class LoginRegisterWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardsPanel;

    // Login Panel Components
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    // Register Panel Components
    private JTextField regNameField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JComboBox<String> regRoleCombo;

    public LoginRegisterWindow() {
        setTitle("Brew & Blend Coffee — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main split panel: Left is Coffee Logo Art, Right is Form Cards
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(Theme.PRIMARY_LIGHT);

        // Left Panel: Premium Animated Coffee Art & Welcome
        JPanel leftPanel = new JPanel() {
            private float steamOffset = 0f;
            private Timer steamTimer;
            {
                steamTimer = new Timer(50, e -> {
                    steamOffset += 0.08f;
                    if (steamOffset > Math.PI * 2) steamOffset = 0;
                    repaint();
                });
                steamTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Rich gradient background
                GradientPaint bgGp = new GradientPaint(0, 0, Theme.PRIMARY_DARK,
                        w, h, new Color(0x2C, 0x1A, 0x12));
                g2.setPaint(bgGp);
                g2.fillRect(0, 0, w, h);

                // Subtle decorative circles
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(-50, -50, 250, 250);
                g2.fillOval(w - 120, h - 180, 200, 200);
                g2.setColor(new Color(212, 165, 116, 15)); // Gold tint
                g2.fillOval(w / 2 - 100, h / 2 - 100, 200, 200);

                // Draw artistic Coffee Cup
                int cx = w / 2;
                int cy = h / 2 - 10;

                // Plate/Saucer
                g2.setColor(new Color(0xFF, 0xF0, 0xDB));
                g2.fillOval(cx - 85, cy + 42, 170, 22);
                g2.setColor(new Color(0xD4, 0xA5, 0x74, 60));
                g2.drawOval(cx - 85, cy + 42, 170, 22);

                // Cup Body
                g2.setColor(new Color(0xFF, 0xF0, 0xDB));
                g2.fillArc(cx - 65, cy - 22, 130, 105, 180, 180);

                // Cup Rim
                g2.setColor(new Color(0xE8, 0xD8, 0xC8));
                g2.fillOval(cx - 65, cy - 32, 130, 22);

                // Coffee Liquid inside cup
                GradientPaint coffeeGp = new GradientPaint(cx - 55, cy - 24, new Color(0x3E, 0x27, 0x23),
                        cx + 55, cy - 16, new Color(0x5D, 0x40, 0x37));
                g2.setPaint(coffeeGp);
                g2.fillOval(cx - 55, cy - 26, 110, 16);

                // Cup Handle
                g2.setColor(new Color(0xFF, 0xF0, 0xDB));
                g2.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx + 45, cy - 12, 38, 48, -90, 180);

                // Animated Steam lines
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = -1; i <= 1; i++) {
                    int sx = cx + (i * 22);
                    int sy = cy - 48;
                    float offset = steamOffset + (i * 0.8f);
                    GeneralPath steam = new GeneralPath();
                    steam.moveTo(sx, sy);
                    steam.quadTo(sx - 10 * Math.sin(offset), sy - 22, sx + 5 * Math.cos(offset), sy - 42);
                    steam.quadTo(sx + 12 * Math.sin(offset + 1), sy - 60, sx - 3 * Math.cos(offset), sy - 80);
                    g2.draw(steam);
                }

                g2.dispose();
            }

            @Override
            public void removeNotify() {
                super.removeNotify();
                if (steamTimer != null) steamTimer.stop();
            }
        };
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Brand text at bottom of left panel
        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));

        JLabel brandName = new JLabel("BREW & BLEND");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brandName.setForeground(Theme.GOLD_ACCENT);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandSub = new JLabel("Coffee Shop Management System");
        brandSub.setFont(Theme.FONT_BODY);
        brandSub.setForeground(new Color(0xB0, 0x9B, 0x93));
        brandSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Decorative divider
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int cx = getWidth() / 2;
                GradientPaint gp = new GradientPaint(cx - 30, 0, new Color(0, 0, 0, 0), cx, 0, Theme.GOLD_ACCENT);
                g2.setPaint(gp);
                g2.fillRect(cx - 30, 0, 30, getHeight());
                GradientPaint gp2 = new GradientPaint(cx, 0, Theme.GOLD_ACCENT, cx + 30, 0, new Color(0, 0, 0, 0));
                g2.setPaint(gp2);
                g2.fillRect(cx, 0, 30, getHeight());
                g2.dispose();
            }
        };
        divider.setPreferredSize(new Dimension(60, 2));
        divider.setMaximumSize(new Dimension(60, 2));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(brandName);
        brandPanel.add(Box.createVerticalStrut(6));
        brandPanel.add(divider);
        brandPanel.add(Box.createVerticalStrut(6));
        brandPanel.add(brandSub);

        leftPanel.add(brandPanel, BorderLayout.SOUTH);

        // Right Panel: Form Cards (Login / Register)
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(Theme.BG_SECTION);

        // Create Login Card
        JPanel loginCard = createLoginCard();
        // Create Register Card
        JPanel registerCard = createRegisterCard();

        cardsPanel.add(loginCard, "LOGIN");
        cardsPanel.add(registerCard, "REGISTER");

        mainPanel.add(leftPanel);
        mainPanel.add(cardsPanel);

        add(mainPanel);

        // Default show Login
        cardLayout.show(cardsPanel, "LOGIN");
    }

    private JPanel createLoginCard() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(Theme.BG_SECTION);

        JPanel card = Theme.createShadowCardPanel(Theme.BG_CARD);
        card.setPreferredSize(new Dimension(380, 440));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Log in to your account");
        subtitle.setFont(Theme.FONT_SMALL_ITALIC);
        subtitle.setForeground(Theme.GOLD_ACCENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginEmailField = Theme.createStyledTextField("Email Address");
        loginEmailField.setMaximumSize(new Dimension(310, 45));
        loginEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginPasswordField = Theme.createStyledPasswordField("Password");
        loginPasswordField.setMaximumSize(new Dimension(310, 45));
        loginPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = Theme.createGradientButton("Sign In", Theme.PRIMARY_DARK, Theme.PRIMARY_DARK_ALT, Theme.TEXT_LIGHT);
        loginBtn.setMaximumSize(new Dimension(310, 48));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(Theme.FONT_SMALL);
        noAccountLabel.setForeground(Theme.TEXT_SECONDARY);

        JButton toRegisterBtn = new JButton("Register Here");
        toRegisterBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toRegisterBtn.setForeground(Theme.GOLD_ACCENT);
        toRegisterBtn.setBorderPainted(false);
        toRegisterBtn.setContentAreaFilled(false);
        toRegisterBtn.setFocusPainted(false);
        toRegisterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toRegisterBtn.addActionListener(e -> {
            clearFields();
            cardLayout.show(cardsPanel, "REGISTER");
        });

        footer.add(noAccountLabel);
        footer.add(toRegisterBtn);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacing
        card.add(Box.createVerticalStrut(25));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(35));
        card.add(loginEmailField);
        card.add(Box.createVerticalStrut(15));
        card.add(loginPasswordField);
        card.add(Box.createVerticalStrut(30));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(footer);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        outerPanel.add(card, gbc);

        return outerPanel;
    }

    private JPanel createRegisterCard() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(Theme.BG_SECTION);

        JPanel card = Theme.createShadowCardPanel(Theme.BG_CARD);
        card.setPreferredSize(new Dimension(380, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Create Account");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join our coffee community");
        subtitle.setFont(Theme.FONT_SMALL_ITALIC);
        subtitle.setForeground(Theme.GOLD_ACCENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        regNameField = Theme.createStyledTextField("Full Name");
        regNameField.setMaximumSize(new Dimension(310, 45));
        regNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        regEmailField = Theme.createStyledTextField("Email Address");
        regEmailField.setMaximumSize(new Dimension(310, 45));
        regEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        regPasswordField = Theme.createStyledPasswordField("Password");
        regPasswordField.setMaximumSize(new Dimension(310, 45));
        regPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Combobox for role selection
        regRoleCombo = new JComboBox<>(new String[]{"Customer", "Admin"});
        regRoleCombo.setFont(Theme.FONT_BODY);
        regRoleCombo.setMaximumSize(new Dimension(310, 45));
        regRoleCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        regRoleCombo.setBackground(Theme.BG_WHITE);
        regRoleCombo.setForeground(Theme.TEXT_DARK);

        JButton regBtn = Theme.createGradientButton("Sign Up", Theme.PRIMARY_DARK, Theme.PRIMARY_DARK_ALT, Theme.TEXT_LIGHT);
        regBtn.setMaximumSize(new Dimension(310, 48));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(Theme.FONT_SMALL);
        haveAccountLabel.setForeground(Theme.TEXT_SECONDARY);

        JButton toLoginBtn = new JButton("Login Now");
        toLoginBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toLoginBtn.setForeground(Theme.GOLD_ACCENT);
        toLoginBtn.setBorderPainted(false);
        toLoginBtn.setContentAreaFilled(false);
        toLoginBtn.setFocusPainted(false);
        toLoginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toLoginBtn.addActionListener(e -> {
            clearFields();
            cardLayout.show(cardsPanel, "LOGIN");
        });

        footer.add(haveAccountLabel);
        footer.add(toLoginBtn);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacing
        card.add(Box.createVerticalStrut(20));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));
        card.add(regNameField);
        card.add(Box.createVerticalStrut(12));
        card.add(regEmailField);
        card.add(Box.createVerticalStrut(12));
        card.add(regPasswordField);
        card.add(Box.createVerticalStrut(12));
        card.add(regRoleCombo);
        card.add(Box.createVerticalStrut(25));
        card.add(regBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(footer);

        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        outerPanel.add(card, gbc);

        return outerPanel;
    }

    private void clearFields() {
        loginEmailField.setText("");
        loginPasswordField.setText("");
        regNameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        regRoleCombo.setSelectedIndex(0);
    }

    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Email and Password.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = DatabaseManager.loginUser(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome back, " + user.getName() + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

            // Open appropriate dashboard
            if (user.isAdmin()) {
                SwingUtilities.invokeLater(() -> new AdminDashboard(user).setVisible(true));
            } else {
                SwingUtilities.invokeLater(() -> new CustomerDashboard(user).setVisible(true));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String name = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = new String(regPasswordField.getPassword()).trim();
        String role = (String) regRoleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = DatabaseManager.registerUser(name, email, password, role);
        if (success) {
            JOptionPane.showMessageDialog(this, "Account created successfully! Please log in.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            cardLayout.show(cardsPanel, "LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Email may already be in use.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
