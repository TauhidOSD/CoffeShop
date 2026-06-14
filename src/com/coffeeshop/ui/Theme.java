package com.coffeeshop.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class Theme {
    // ============================================================
    // PREMIUM COFFEE COLOR PALETTE
    // ============================================================
    public static final Color PRIMARY_DARK = new Color(0x1A, 0x0E, 0x0A);         // Deep Espresso Black
    public static final Color PRIMARY_DARK_ALT = new Color(0x2C, 0x1A, 0x12);     // Rich Dark Brown
    public static final Color PRIMARY_LIGHT = new Color(0xFF, 0xF8, 0xE1);        // Warm Cream
    public static final Color PRIMARY_ACCENT = new Color(0x79, 0x55, 0x48);       // Coffee Brown
    public static final Color PRIMARY_ACCENT_HOVER = new Color(0x5D, 0x40, 0x37); // Darker Brown
    public static final Color GOLD_ACCENT = new Color(0xD4, 0xA5, 0x74);          // Premium Gold
    public static final Color GOLD_LIGHT = new Color(0xE8, 0xC5, 0x9A);           // Light Gold
    public static final Color WARM_AMBER = new Color(0xE8, 0xA8, 0x7C);           // Warm Amber
    public static final Color BG_CARD = new Color(0xFB, 0xF7, 0xF4);              // Warm White Card
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BG_SECTION = new Color(0xF5, 0xF0, 0xEB);           // Section Background
    public static final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);            // Near Black
    public static final Color TEXT_SECONDARY = new Color(0x6B, 0x5B, 0x50);       // Muted Brown
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(0xE8, 0xDD, 0xD4);         // Warm Border
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 35);              // Subtle Shadow

    // Status colors
    public static final Color STATUS_PENDING = new Color(255, 152, 0);    // Amber
    public static final Color STATUS_PREPARING = new Color(33, 150, 243); // Blue
    public static final Color STATUS_DELIVERED = new Color(76, 175, 80);  // Green

    // ============================================================
    // PREMIUM TYPOGRAPHY
    // ============================================================
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 38);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_ITALIC = new Font("Segoe UI", Font.ITALIC, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BRAND = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_NAV = new Font("Segoe UI", Font.BOLD, 14);

    // ============================================================
    // CUSTOM ROUNDED BUTTON WITH MICRO-ANIMATIONS
    // ============================================================
    public static JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            private float hoverProgress = 0f;
            private Timer hoverTimer;

            {
                hoverTimer = new Timer(16, null);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded background
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

                // Draw text
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover visual effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    // ============================================================
    // GRADIENT BUTTON — Premium CTA Style
    // ============================================================
    public static JButton createGradientButton(String text, Color startColor, Color endColor, Color fg) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14);

                // Gradient background
                Color s = isHovered ? startColor.brighter() : startColor;
                Color e = isHovered ? endColor.brighter() : endColor;
                GradientPaint gp = new GradientPaint(0, 0, s, getWidth(), getHeight(), e);
                g2.setPaint(gp);
                g2.fill(shape);

                // Subtle inner glow when hovered
                if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() / 2, 12, 12));
                }

                // Draw text
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };

        button.setFont(FONT_BODY_BOLD);
        button.setForeground(fg);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ============================================================
    // NAVBAR LINK WITH ANIMATED UNDERLINE
    // ============================================================
    public static JButton createNavbarLink(String text) {
        JButton button = new JButton(text) {
            private float underlineWidth = 0f;
            private Timer animTimer;
            private boolean isHovering = false;

            {
                animTimer = new Timer(16, e -> {
                    if (isHovering && underlineWidth < 1f) {
                        underlineWidth = Math.min(1f, underlineWidth + 0.12f);
                        repaint();
                    } else if (!isHovering && underlineWidth > 0f) {
                        underlineWidth = Math.max(0f, underlineWidth - 0.12f);
                        repaint();
                    } else {
                        ((Timer) e.getSource()).stop();
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovering = true;
                        setForeground(GOLD_ACCENT);
                        if (!animTimer.isRunning()) animTimer.start();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovering = false;
                        setForeground(new Color(0xE0, 0xD5, 0xCC));
                        if (!animTimer.isRunning()) animTimer.start();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw text
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);

                // Draw animated underline
                if (underlineWidth > 0) {
                    int lineW = (int) (textWidth * underlineWidth);
                    int lineX = x + (textWidth - lineW) / 2;
                    int lineY = y + 4;
                    g2.setColor(GOLD_ACCENT);
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(lineX, lineY, lineX + lineW, lineY);
                }

                g2.dispose();
            }
        };

        button.setFont(FONT_NAV);
        button.setForeground(new Color(0xE0, 0xD5, 0xCC));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ============================================================
    // CARD PANEL WITH DROP SHADOW
    // ============================================================
    public static JPanel createShadowCardPanel(Color bg) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowSize = 6;
                int arc = 20;

                // Draw multi-layered soft shadow
                for (int i = shadowSize; i >= 1; i--) {
                    float alpha = (float) (0.06 * (shadowSize - i + 1));
                    g2.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
                    g2.fill(new RoundRectangle2D.Float(i, i + 2, getWidth() - i * 2, getHeight() - i * 2, arc, arc));
                }

                // Draw card background
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, arc, arc));

                g2.dispose();
            }
        };
        panel.setBackground(bg);
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    // ============================================================
    // ANIMATED HOVER CARD — Lifts on hover with deeper shadow
    // ============================================================
    public static JPanel createHoverCard(Color bg) {
        JPanel panel = new JPanel() {
            private int yOffset = 0;
            private int shadowDepth = 4;
            private Timer hoverInTimer;
            private Timer hoverOutTimer;

            {
                hoverInTimer = new Timer(16, e -> {
                    boolean changed = false;
                    if (yOffset > -4) { yOffset--; changed = true; }
                    if (shadowDepth < 12) { shadowDepth++; changed = true; }
                    if (changed) repaint(); else hoverInTimer.stop();
                });

                hoverOutTimer = new Timer(16, e -> {
                    boolean changed = false;
                    if (yOffset < 0) { yOffset++; changed = true; }
                    if (shadowDepth > 4) { shadowDepth--; changed = true; }
                    if (changed) repaint(); else hoverOutTimer.stop();
                });

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverOutTimer.stop();
                        if (!hoverInTimer.isRunning()) hoverInTimer.start();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverInTimer.stop();
                        if (!hoverOutTimer.isRunning()) hoverOutTimer.start();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 20;
                int pad = 8;

                // Draw shadow layers
                for (int i = shadowDepth; i >= 1; i--) {
                    float alpha = (float) (0.04 * (shadowDepth - i + 1));
                    g2.setColor(new Color(0, 0, 0, Math.min(255, (int) (alpha * 255))));
                    g2.fill(new RoundRectangle2D.Float(
                            pad + i, pad + i + 2 + yOffset,
                            getWidth() - pad * 2 - i * 2,
                            getHeight() - pad * 2 - i * 2,
                            arc, arc));
                }

                // Draw card
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(
                        pad, pad + yOffset,
                        getWidth() - pad * 2,
                        getHeight() - pad * 2,
                        arc, arc));

                // Subtle border
                g2.setColor(new Color(0, 0, 0, 15));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(
                        pad, pad + yOffset,
                        getWidth() - pad * 2 - 1,
                        getHeight() - pad * 2 - 1,
                        arc, arc));

                g2.dispose();
            }
        };
        panel.setBackground(bg);
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    // ============================================================
    // LEGACY CARD PANEL (for compatibility)
    // ============================================================
    public static JPanel createCardPanel(Color bg) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        panel.setBackground(bg);
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        return panel;
    }

    // ============================================================
    // STYLED TEXT FIELD WITH PLACEHOLDER
    // ============================================================
    public static JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0x9E, 0x8E, 0x82));
                    g2.setFont(getFont());
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int x = insets.left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };

        textField.setFont(FONT_BODY);
        textField.setForeground(TEXT_DARK);
        textField.setBackground(BG_WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD_ACCENT, 2, true),
                        new EmptyBorder(9, 13, 9, 13)
                ));
                textField.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(10, 14, 10, 14)
                ));
                textField.repaint();
            }
        });

        return textField;
    }

    // ============================================================
    // STYLED PASSWORD FIELD WITH PLACEHOLDER
    // ============================================================
    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                String passStr = new String(getPassword());
                if (passStr.isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0x9E, 0x8E, 0x82));
                    g2.setFont(getFont());
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int x = insets.left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };

        passwordField.setFont(FONT_BODY);
        passwordField.setForeground(TEXT_DARK);
        passwordField.setBackground(BG_WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD_ACCENT, 2, true),
                        new EmptyBorder(9, 13, 9, 13)
                ));
                passwordField.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(10, 14, 10, 14)
                ));
                passwordField.repaint();
            }
        });

        return passwordField;
    }

    // ============================================================
    // MODERN TABLE SETUP
    // ============================================================
    public static void setupTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xE8, 0xDD, 0xD4));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(0xF0, 0xF0, 0xF0));

        // Customize header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(PRIMARY_DARK);
        header.setForeground(TEXT_LIGHT);
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        // Customize cell rendering with alternating rows
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(BG_WHITE);
                    } else {
                        c.setBackground(new Color(0xFB, 0xF7, 0xF4));
                    }
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ============================================================
    // GRADIENT PANEL UTILITY
    // ============================================================
    public static JPanel createGradientPanel(Color startColor, Color endColor, boolean vertical) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp;
                if (vertical) {
                    gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                } else {
                    gp = new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
                }
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    // ============================================================
    // PRICE BADGE — Gradient rounded label
    // ============================================================
    public static JLabel createPriceBadge(String priceText) {
        JLabel label = new JLabel(priceText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, GOLD_ACCENT, getWidth(), getHeight(), WARM_AMBER);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Draw text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        label.setFont(FONT_BODY_BOLD);
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(80, 30));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
}
