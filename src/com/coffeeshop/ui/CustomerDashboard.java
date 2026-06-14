package com.coffeeshop.ui;

import com.coffeeshop.database.DatabaseManager;
import com.coffeeshop.model.Feedback;
import com.coffeeshop.model.MenuItem;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CustomerDashboard extends JFrame {
    private final User currentUser;
    private final List<CartItem> cart = new ArrayList<>();

    // Navigation and Panels
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    private JButton navCartBtn;

    private static final String PAGE_MENU = "PAGE_MENU";
    private static final String PAGE_CART = "PAGE_CART";
    private static final String PAGE_HISTORY = "PAGE_HISTORY";
    private static final String PAGE_FEEDBACK = "PAGE_FEEDBACK";

    // Menu Tab components
    private JPanel menuGridPanel;
    private JTextField searchField;

    // Cart Tab components
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JTextField promoField;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;
    private String activeDiscountCode = "";
    private double discountRate = 0.0;
    private double flatDiscount = 0.0;

    // History Tab components
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Cart Item Helper Class
    private static class CartItem {
        MenuItem item;
        int quantity;

        CartItem(MenuItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        double getSubtotal() {
            return item.getPrice() * quantity;
        }
    }

    public CustomerDashboard(User user) {
        this.currentUser = user;

        setTitle("Brew & Blend Coffee — " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.BG_SECTION);

        // 1. PREMIUM GRADIENT NAVBAR
        JPanel navbar = createPremiumNavbar();

        // 2. Main Content Card Layout Panel
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(Theme.BG_SECTION);

        // Adding Pages to contentPanel
        contentPanel.add(createMenuPageScrollable(), PAGE_MENU);
        contentPanel.add(createCartTab(), PAGE_CART);
        contentPanel.add(createHistoryTab(), PAGE_HISTORY);
        contentPanel.add(createFeedbackTab(), PAGE_FEEDBACK);

        mainContainer.add(navbar, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    // ============================================================
    // PREMIUM GRADIENT NAVBAR
    // ============================================================
    private JPanel createPremiumNavbar() {
        JPanel navbar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY_DARK, getWidth(), 0, Theme.PRIMARY_DARK_ALT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Bottom gold accent line
                g2.setColor(Theme.GOLD_ACCENT);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);

                g2.dispose();
            }
        };
        navbar.setBorder(new EmptyBorder(12, 30, 14, 30));
        navbar.setPreferredSize(new Dimension(0, 60));

        // Brand Logo
        JLabel logoLabel = new JLabel("☕  BREW & BLEND") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();

                // Gold gradient text
                GradientPaint textGp = new GradientPaint(0, 0, Theme.GOLD_ACCENT, fm.stringWidth(getText()), 0, Theme.GOLD_LIGHT);
                g2.setPaint(textGp);
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }
        };
        logoLabel.setFont(Theme.FONT_BRAND);
        logoLabel.setForeground(Theme.GOLD_ACCENT);

        // Navigation Links Panel
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        linksPanel.setOpaque(false);

        JButton navMenuBtn = Theme.createNavbarLink("☕ Menu");
        navCartBtn = Theme.createNavbarLink("🛒 Cart (" + cart.size() + ")");
        JButton navHistoryBtn = Theme.createNavbarLink("📋 Orders");
        JButton navFeedbackBtn = Theme.createNavbarLink("💬 Feedback");

        linksPanel.add(navMenuBtn);
        linksPanel.add(Box.createHorizontalStrut(5));
        linksPanel.add(navCartBtn);
        linksPanel.add(Box.createHorizontalStrut(5));
        linksPanel.add(navHistoryBtn);
        linksPanel.add(Box.createHorizontalStrut(5));
        linksPanel.add(navFeedbackBtn);

        // Action listeners for nav links
        navMenuBtn.addActionListener(e -> showPage(PAGE_MENU));
        navCartBtn.addActionListener(e -> showPage(PAGE_CART));
        navHistoryBtn.addActionListener(e -> showPage(PAGE_HISTORY));
        navFeedbackBtn.addActionListener(e -> showPage(PAGE_FEEDBACK));

        // User Info & Logout
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userInfoPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setFont(Theme.FONT_SMALL);
        welcomeLabel.setForeground(Theme.GOLD_LIGHT);

        JButton logoutBtn = Theme.createGradientButton("Logout", Theme.PRIMARY_ACCENT, Theme.PRIMARY_ACCENT_HOVER, Theme.TEXT_LIGHT);
        logoutBtn.setPreferredSize(new Dimension(90, 32));
        logoutBtn.addActionListener(e -> handleLogout());

        userInfoPanel.add(welcomeLabel);
        userInfoPanel.add(logoutBtn);

        navbar.add(logoLabel, BorderLayout.WEST);
        navbar.add(linksPanel, BorderLayout.CENTER);
        navbar.add(userInfoPanel, BorderLayout.EAST);

        return navbar;
    }

    // Page switching helper
    private void showPage(String pageName) {
        contentCardLayout.show(contentPanel, pageName);
        if (pageName.equals(PAGE_CART)) {
            refreshCartTable();
        } else if (pageName.equals(PAGE_HISTORY)) {
            loadOrderHistory();
        }
    }

    // ============================================================
    // IMAGE LOADER FOR CARD GRID
    // ============================================================
    private ImageIcon getMenuItemImage(String itemName, int width, int height) {
        String name = itemName.toLowerCase();
        String fileBase = "";
        if (name.contains("espresso")) {
            fileBase = "espresso.png";
        } else if (name.contains("cappuccino")) {
            fileBase = "cappuccino.png";
        } else if (name.contains("latte")) {
            fileBase = "latte.png";
        } else if (name.contains("macchiato")) {
            fileBase = "caramel_macchiato.png";
        } else if (name.contains("americano")) {
            fileBase = "americano.png";
        } else if (name.contains("mocha")) {
            fileBase = "mocha.png";
        } else if (name.contains("cold brew")) {
            fileBase = "cold_brew.png";
        } else if (name.contains("croissant")) {
            fileBase = "croissant.png";
        } else if (name.contains("muffin")) {
            fileBase = "muffin.png";
        } else {
            fileBase = "espresso.png";
        }

        File imgFile = new File("assets/" + fileBase);
        if (!imgFile.exists()) {
            return null;
        }

        try {
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Failed to load image " + fileBase + ": " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // SMOOTH SLIDING IMAGE CAROUSEL (HERO SLIDER)
    // ============================================================
    public static class PremiumSliderPanel extends JPanel {
        private List<Image> images = new ArrayList<>();
        private int currentIndex = 0;
        private int nextIndex = 0;
        private boolean isAnimating = false;
        private double progress = 0.0;
        private Timer slideTimer;
        private Timer switchTimer;

        // Slide info for overlay text
        private String[] slideHeadlines = {
                "Start Your Day with\nthe Perfect Brew",
                "Handcrafted\nEspresso Perfection",
                "Silky Smooth\nLatte Artistry",
                "Caramel Macchiato\nIndulgence",
                "Rich & Bold\nMocha Delight"
        };
        private String[] slideSubtitles = {
                "Brewed with passion, served with love",
                "100% Arabica beans, roasted to perfection",
                "Creamy milk meets bold espresso",
                "Sweet caramel drizzle in every sip",
                "Chocolate and coffee in perfect harmony"
        };

        public PremiumSliderPanel() {
            setBackground(Theme.PRIMARY_DARK);
            setOpaque(true);

            // Load slider images
            String[] imgFiles = {"cappuccino.png", "espresso.png", "latte.png", "caramel_macchiato.png", "mocha.png"};
            for (String file : imgFiles) {
                try {
                    File f = new File("assets/" + file);
                    if (f.exists()) {
                        images.add(ImageIO.read(f));
                    }
                } catch (Exception e) {
                    System.err.println("Error reading slider image: " + file);
                }
            }

            // Smooth slide animation (easing)
            slideTimer = new Timer(16, e -> {
                progress += 0.025; // Slower, smoother
                // Apply ease-out curve
                double easedProgress = 1.0 - Math.pow(1.0 - Math.min(progress, 1.0), 3);
                if (progress >= 1.0) {
                    slideTimer.stop();
                    currentIndex = nextIndex;
                    isAnimating = false;
                    progress = 0.0;
                }
                repaint();
            });

            // Change image every 5 seconds
            switchTimer = new Timer(5000, e -> {
                if (images.size() > 1 && !isAnimating) {
                    nextIndex = (currentIndex + 1) % images.size();
                    progress = 0.0;
                    isAnimating = true;
                    slideTimer.start();
                }
            });
            switchTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (images.isEmpty()) {
                g.setColor(Theme.PRIMARY_ACCENT);
                g.setFont(Theme.FONT_TITLE);
                g.drawString("Brewing delicious coffee...", 40, getHeight() / 2);
                return;
            }

            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Ease-out curve for smooth deceleration
            double easedProgress = isAnimating ? (1.0 - Math.pow(1.0 - Math.min(progress, 1.0), 3)) : 0;

            // Draw images with horizontal slide
            if (isAnimating) {
                int offset = (int) (easedProgress * w);
                g2.drawImage(images.get(currentIndex), -offset, 0, w, h, null);
                g2.drawImage(images.get(nextIndex), w - offset, 0, w, h, null);
            } else {
                g2.drawImage(images.get(currentIndex), 0, 0, w, h, null);
            }

            // Dark gradient overlay (left to right + bottom)
            GradientPaint leftOverlay = new GradientPaint(0, 0, new Color(0, 0, 0, 180), (int) (w * 0.6), 0, new Color(0, 0, 0, 30));
            g2.setPaint(leftOverlay);
            g2.fillRect(0, 0, w, h);

            GradientPaint bottomOverlay = new GradientPaint(0, (int) (h * 0.6), new Color(0, 0, 0, 0), 0, h, new Color(0, 0, 0, 180));
            g2.setPaint(bottomOverlay);
            g2.fillRect(0, 0, w, h);

            // Draw headline text
            int textIndex = isAnimating ? nextIndex : currentIndex;
            if (textIndex < slideHeadlines.length) {
                // Headline
                g2.setFont(Theme.FONT_HERO);
                String[] lines = slideHeadlines[textIndex].split("\n");
                int textY = h / 2 - 40;
                for (String line : lines) {
                    // Text shadow
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.drawString(line, 62, textY + 2);
                    // Gold gradient text
                    g2.setColor(Color.WHITE);
                    g2.drawString(line, 60, textY);
                    textY += 48;
                }

                // Subtitle
                g2.setFont(Theme.FONT_BODY);
                g2.setColor(Theme.GOLD_LIGHT);
                if (textIndex < slideSubtitles.length) {
                    g2.drawString(slideSubtitles[textIndex], 60, textY + 15);
                }
            }

            // Draw slide indicator dots
            int dotSize = 10;
            int dotSpacing = 18;
            int dotsWidth = images.size() * dotSpacing;
            int dotsX = (w - dotsWidth) / 2;
            int dotsY = h - 30;

            for (int i = 0; i < images.size(); i++) {
                int dx = dotsX + i * dotSpacing;
                boolean isActive = (isAnimating ? nextIndex : currentIndex) == i;
                if (isActive) {
                    g2.setColor(Theme.GOLD_ACCENT);
                    g2.fillRoundRect(dx, dotsY, 22, dotSize, dotSize, dotSize);
                } else {
                    g2.setColor(new Color(255, 255, 255, 120));
                    g2.fillOval(dx + 6, dotsY, dotSize, dotSize);
                }
            }

            g2.dispose();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            if (slideTimer != null) slideTimer.stop();
            if (switchTimer != null) switchTimer.stop();
        }
    }

    // ============================================================
    // HERO PANEL CREATION
    // ============================================================
    private JPanel createHeroSection() {
        // Full-width hero with slider
        JPanel heroWrapper = new JPanel(new BorderLayout());
        heroWrapper.setPreferredSize(new Dimension(0, 400));
        heroWrapper.setBackground(Theme.PRIMARY_DARK);

        PremiumSliderPanel slider = new PremiumSliderPanel();
        heroWrapper.add(slider, BorderLayout.CENTER);

        return heroWrapper;
    }

    // ============================================================
    // PROFESSIONAL FOOTER
    // ============================================================
    private JPanel createFooterPanel() {
        JPanel footerContainer = new JPanel(new BorderLayout());
        footerContainer.setBackground(Theme.PRIMARY_DARK);

        // Top accent gradient line
        JPanel accentLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Theme.GOLD_ACCENT, getWidth(), 0, Theme.WARM_AMBER);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        accentLine.setPreferredSize(new Dimension(0, 3));
        footerContainer.add(accentLine, BorderLayout.NORTH);

        // Main footer content
        JPanel footer = new JPanel(new GridLayout(1, 4, 30, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY_DARK, 0, getHeight(), new Color(0x0D, 0x07, 0x05));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        footer.setBorder(new EmptyBorder(40, 50, 30, 50));

        // === Column 1: Brand ===
        JPanel brandCol = new JPanel();
        brandCol.setOpaque(false);
        brandCol.setLayout(new BoxLayout(brandCol, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("☕ BREW & BLEND");
        logo.setFont(Theme.FONT_TITLE);
        logo.setForeground(Theme.GOLD_ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html>Your favorite neighborhood<br>coffee shop. Handcrafting<br>premium blends since 2026.</html>");
        tagline.setFont(Theme.FONT_SMALL);
        tagline.setForeground(new Color(0x8A, 0x7B, 0x72));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Social icons
        JPanel socialPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        socialPanel.setOpaque(false);
        socialPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] socials = {"📘 Facebook", "📸 Instagram", "🐦 Twitter"};
        for (String s : socials) {
            JButton socialBtn = new JButton(s);
            socialBtn.setFont(Theme.FONT_TINY);
            socialBtn.setForeground(new Color(0x9E, 0x8E, 0x82));
            socialBtn.setContentAreaFilled(false);
            socialBtn.setBorderPainted(false);
            socialBtn.setFocusPainted(false);
            socialBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            socialBtn.setMargin(new Insets(2, 0, 2, 0));
            socialBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { socialBtn.setForeground(Theme.GOLD_ACCENT); }
                @Override
                public void mouseExited(MouseEvent e) { socialBtn.setForeground(new Color(0x9E, 0x8E, 0x82)); }
            });
            socialPanel.add(socialBtn);
        }

        brandCol.add(logo);
        brandCol.add(Box.createVerticalStrut(12));
        brandCol.add(tagline);
        brandCol.add(Box.createVerticalStrut(16));
        brandCol.add(socialPanel);

        // === Column 2: Quick Links ===
        JPanel linksCol = new JPanel();
        linksCol.setOpaque(false);
        linksCol.setLayout(new BoxLayout(linksCol, BoxLayout.Y_AXIS));

        JLabel linksTitle = new JLabel("QUICK LINKS");
        linksTitle.setFont(Theme.FONT_BODY_BOLD);
        linksTitle.setForeground(Theme.GOLD_ACCENT);
        linksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linksDivider = createFooterDivider();
        linksDivider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton footerMenu = createFooterLink("→  Explore Coffee Menu");
        footerMenu.addActionListener(e -> showPage(PAGE_MENU));

        JButton footerCart = createFooterLink("→  View My Cart");
        footerCart.addActionListener(e -> showPage(PAGE_CART));

        JButton footerOrders = createFooterLink("→  Order History");
        footerOrders.addActionListener(e -> showPage(PAGE_HISTORY));

        JButton footerFeedback = createFooterLink("→  Send Us Feedback");
        footerFeedback.addActionListener(e -> showPage(PAGE_FEEDBACK));

        linksCol.add(linksTitle);
        linksCol.add(Box.createVerticalStrut(8));
        linksCol.add(linksDivider);
        linksCol.add(Box.createVerticalStrut(10));
        linksCol.add(footerMenu);
        linksCol.add(footerCart);
        linksCol.add(footerOrders);
        linksCol.add(footerFeedback);

        // === Column 3: Contact Info ===
        JPanel contactCol = new JPanel();
        contactCol.setOpaque(false);
        contactCol.setLayout(new BoxLayout(contactCol, BoxLayout.Y_AXIS));

        JLabel contactTitle = new JLabel("CONTACT US");
        contactTitle.setFont(Theme.FONT_BODY_BOLD);
        contactTitle.setForeground(Theme.GOLD_ACCENT);
        contactTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel contactDivider = createFooterDivider();
        contactDivider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel address = new JLabel("<html>📍  123 Espresso Blvd<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Dhaka, Bangladesh</html>");
        address.setFont(Theme.FONT_SMALL);
        address.setForeground(new Color(0x9E, 0x8E, 0x82));
        address.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel phone = new JLabel("📞  +880 1234 567890");
        phone.setFont(Theme.FONT_SMALL);
        phone.setForeground(new Color(0x9E, 0x8E, 0x82));
        phone.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel email = new JLabel("✉️  hello@brewblend.com");
        email.setFont(Theme.FONT_SMALL);
        email.setForeground(new Color(0x9E, 0x8E, 0x82));
        email.setAlignmentX(Component.LEFT_ALIGNMENT);

        contactCol.add(contactTitle);
        contactCol.add(Box.createVerticalStrut(8));
        contactCol.add(contactDivider);
        contactCol.add(Box.createVerticalStrut(10));
        contactCol.add(address);
        contactCol.add(Box.createVerticalStrut(8));
        contactCol.add(phone);
        contactCol.add(Box.createVerticalStrut(8));
        contactCol.add(email);

        // === Column 4: Opening Hours ===
        JPanel hoursCol = new JPanel();
        hoursCol.setOpaque(false);
        hoursCol.setLayout(new BoxLayout(hoursCol, BoxLayout.Y_AXIS));

        JLabel hoursTitle = new JLabel("OPENING HOURS");
        hoursTitle.setFont(Theme.FONT_BODY_BOLD);
        hoursTitle.setForeground(Theme.GOLD_ACCENT);
        hoursTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel hoursDivider = createFooterDivider();
        hoursDivider.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] hourItems = {
                "Mon - Fri :  7:00 AM - 9:00 PM",
                "Saturday  :  8:00 AM - 10:00 PM",
                "Sunday    :  9:00 AM - 8:00 PM"
        };
        hoursCol.add(hoursTitle);
        hoursCol.add(Box.createVerticalStrut(8));
        hoursCol.add(hoursDivider);
        hoursCol.add(Box.createVerticalStrut(10));
        for (String h : hourItems) {
            JLabel hourLabel = new JLabel("🕐  " + h);
            hourLabel.setFont(Theme.FONT_SMALL);
            hourLabel.setForeground(new Color(0x9E, 0x8E, 0x82));
            hourLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            hoursCol.add(hourLabel);
            hoursCol.add(Box.createVerticalStrut(6));
        }

        footer.add(brandCol);
        footer.add(linksCol);
        footer.add(contactCol);
        footer.add(hoursCol);

        // Copyright bar
        JPanel copyrightBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x0D, 0x07, 0x05));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Top border line
                g2.setColor(new Color(0x3A, 0x2A, 0x22));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        copyrightBar.setBorder(new EmptyBorder(15, 50, 15, 50));

        JLabel copyrightLabel = new JLabel("© 2026 Brew & Blend Coffee. All Rights Reserved.", SwingConstants.CENTER);
        copyrightLabel.setFont(Theme.FONT_TINY);
        copyrightLabel.setForeground(new Color(0x6B, 0x5B, 0x50));
        copyrightBar.add(copyrightLabel, BorderLayout.CENTER);

        JLabel madeWithLabel = new JLabel("Made with ☕ & ❤️ in Bangladesh", SwingConstants.RIGHT);
        madeWithLabel.setFont(Theme.FONT_TINY);
        madeWithLabel.setForeground(new Color(0x6B, 0x5B, 0x50));
        copyrightBar.add(madeWithLabel, BorderLayout.EAST);

        footerContainer.add(footer, BorderLayout.CENTER);
        footerContainer.add(copyrightBar, BorderLayout.SOUTH);

        return footerContainer;
    }

    private JPanel createFooterDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Theme.GOLD_ACCENT, getWidth(), 0, new Color(0x3A, 0x2A, 0x22));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        divider.setPreferredSize(new Dimension(100, 2));
        divider.setMaximumSize(new Dimension(150, 2));
        return divider;
    }

    private JButton createFooterLink(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_SMALL);
        btn.setForeground(new Color(0xB0, 0x9B, 0x93));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMargin(new Insets(3, 0, 3, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Theme.GOLD_ACCENT);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(0xB0, 0x9B, 0x93));
            }
        });
        return btn;
    }

    // ============================================================
    // SECTION HEADER WITH DECORATIVE LINE
    // ============================================================
    private JPanel createSectionHeader(String title, String subtitle) {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(30, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Theme.PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Decorative gold line
        JPanel goldLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int cx = getWidth() / 2;
                GradientPaint gp = new GradientPaint(cx - 40, 0, new Color(0, 0, 0, 0), cx, 0, Theme.GOLD_ACCENT);
                g2.setPaint(gp);
                g2.fillRect(cx - 40, 0, 40, getHeight());
                GradientPaint gp2 = new GradientPaint(cx, 0, Theme.GOLD_ACCENT, cx + 40, 0, new Color(0, 0, 0, 0));
                g2.setPaint(gp2);
                g2.fillRect(cx, 0, 40, getHeight());
                g2.dispose();
            }
        };
        goldLine.setPreferredSize(new Dimension(80, 3));
        goldLine.setMaximumSize(new Dimension(80, 3));
        goldLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(Theme.FONT_BODY);
        subLabel.setForeground(Theme.TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(goldLine);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subLabel);

        return headerPanel;
    }

    // ============================================================
    // MAIN SCROLLABLE MENU PAGE
    // ============================================================
    private JScrollPane createMenuPageScrollable() {
        JPanel container = new JPanel();
        container.setBackground(Theme.BG_SECTION);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // 1. Hero banner
        JPanel hero = createHeroSection();

        // 2. Section header
        JPanel sectionHeader = createSectionHeader(
                "Our Crafted Menu",
                "Explore our handpicked selection of premium coffees & treats"
        );

        // 3. Search bar
        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        searchBarPanel.setBorder(new EmptyBorder(0, 40, 15, 40));
        searchBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        searchField = Theme.createStyledTextField("Search coffee, muffins, teas...");
        searchField.setPreferredSize(new Dimension(300, 42));

        JButton searchBtn = Theme.createGradientButton("🔍 Search", Theme.PRIMARY_DARK, Theme.PRIMARY_DARK_ALT, Theme.TEXT_LIGHT);
        searchBtn.setPreferredSize(new Dimension(110, 42));
        searchBtn.addActionListener(e -> refreshMenuGrid());

        JButton clearBtn = Theme.createRoundedButton("Reset", Theme.BORDER_COLOR, Theme.TEXT_DARK);
        clearBtn.setPreferredSize(new Dimension(80, 42));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            refreshMenuGrid();
        });

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchControls.setOpaque(false);
        searchControls.add(searchBtn);
        searchControls.add(clearBtn);

        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchControls, BorderLayout.EAST);

        // 4. Grid Panel
        menuGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        menuGridPanel.setOpaque(false);
        menuGridPanel.setBorder(new EmptyBorder(5, 35, 25, 35));

        // 5. Footer
        JPanel footerPanel = createFooterPanel();

        container.add(hero);
        container.add(sectionHeader);
        container.add(searchBarPanel);
        container.add(menuGridPanel);
        container.add(Box.createVerticalStrut(20));
        container.add(footerPanel);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        refreshMenuGrid();

        return scrollPane;
    }

    // ============================================================
    // PREMIUM COFFEE CARD DESIGN
    // ============================================================
    private void refreshMenuGrid() {
        menuGridPanel.removeAll();
        String query = searchField.getText().trim();
        List<MenuItem> items;
        if (query.isEmpty() || query.equals("Search coffee, muffins, teas...")) {
            items = DatabaseManager.getMenuItems();
        } else {
            items = DatabaseManager.searchMenuItems(query);
        }

        for (MenuItem item : items) {
            // Use animated hover card
            JPanel card = Theme.createHoverCard(Theme.BG_CARD);
            card.setLayout(new BorderLayout(0, 8));
            card.setPreferredSize(new Dimension(280, 380));

            // ---- Image Panel (top of card with rounded corners) ----
            ImageIcon coffeeIcon = getMenuItemImage(item.getItemName(), 280, 170);
            JPanel imgContainer = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                    g2.setColor(Theme.BG_SECTION);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            imgContainer.setOpaque(false);
            imgContainer.setPreferredSize(new Dimension(280, 170));

            if (coffeeIcon != null) {
                JLabel imgLabel = new JLabel(coffeeIcon) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imgContainer.add(imgLabel, BorderLayout.CENTER);
            } else {
                JLabel fallback = new JLabel("☕", SwingConstants.CENTER);
                fallback.setFont(new Font("Segoe UI", Font.BOLD, 52));
                fallback.setForeground(Theme.GOLD_ACCENT);
                imgContainer.add(fallback, BorderLayout.CENTER);
            }
            card.add(imgContainer, BorderLayout.NORTH);

            // ---- Info Panel (center) ----
            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            JLabel nameLabel = new JLabel(item.getItemName());
            nameLabel.setFont(Theme.FONT_SUBTITLE);
            nameLabel.setForeground(Theme.PRIMARY_DARK);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Price badge
            JLabel priceBadge = Theme.createPriceBadge("$" + String.format("%.2f", item.getPrice()));
            priceBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

            infoPanel.add(Box.createVerticalGlue());
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(8));
            infoPanel.add(priceBadge);
            infoPanel.add(Box.createVerticalGlue());

            card.add(infoPanel, BorderLayout.CENTER);

            // ---- Action Panel (bottom) ----
            JPanel actionPanel = new JPanel(new BorderLayout(8, 0));
            actionPanel.setOpaque(false);
            actionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            qtySpinner.setFont(Theme.FONT_BODY);
            qtySpinner.setPreferredSize(new Dimension(55, 36));

            JButton addToCartBtn = Theme.createGradientButton("🛒 Add to Cart", Theme.PRIMARY_DARK, Theme.PRIMARY_DARK_ALT, Theme.TEXT_LIGHT);
            addToCartBtn.setPreferredSize(new Dimension(140, 36));
            addToCartBtn.addActionListener(e -> {
                int qty = (Integer) qtySpinner.getValue();
                addToCart(item, qty);
                qtySpinner.setValue(1);
            });

            actionPanel.add(qtySpinner, BorderLayout.WEST);
            actionPanel.add(addToCartBtn, BorderLayout.CENTER);

            card.add(actionPanel, BorderLayout.SOUTH);

            menuGridPanel.add(card);
        }

        if (items.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No menu items found.", SwingConstants.CENTER);
            noItemsLabel.setFont(Theme.FONT_TITLE);
            noItemsLabel.setForeground(Theme.TEXT_SECONDARY);
            menuGridPanel.setLayout(new BorderLayout());
            menuGridPanel.add(noItemsLabel, BorderLayout.CENTER);
        } else {
            menuGridPanel.setLayout(new GridLayout(0, 3, 15, 15));
        }

        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private void addToCart(MenuItem item, int qty) {
        boolean found = false;
        for (CartItem ci : cart) {
            if (ci.item.getId() == item.getId()) {
                ci.quantity += qty;
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(item, qty));
        }
        if (navCartBtn != null) {
            navCartBtn.setText("🛒 Cart (" + cart.size() + ")");
        }
        JOptionPane.showMessageDialog(this, qty + "x " + item.getItemName() + " added to cart!", "Cart Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // CART TAB CREATION
    // ============================================================
    private JPanel createCartTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Theme.BG_SECTION);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left Side: Cart Items Table
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JLabel cartTitle = new JLabel("🛒  Your Shopping Cart");
        cartTitle.setFont(Theme.FONT_TITLE);
        cartTitle.setForeground(Theme.PRIMARY_DARK);
        cartTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        leftPanel.add(cartTitle, BorderLayout.NORTH);

        String[] columns = {"Item", "Price", "Qty", "Subtotal", "Actions"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartTableModel);
        Theme.setupTable(cartTable);

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        tableControls.setOpaque(false);

        JButton removeBtn = Theme.createRoundedButton("Remove Selected", Theme.PRIMARY_ACCENT, Theme.TEXT_LIGHT);
        removeBtn.addActionListener(e -> removeSelectedCartItem());

        JButton clearCartBtn = Theme.createRoundedButton("Clear Cart", Theme.PRIMARY_DARK, Theme.TEXT_LIGHT);
        clearCartBtn.addActionListener(e -> clearCart());

        tableControls.add(removeBtn);
        tableControls.add(clearCartBtn);

        leftPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        leftPanel.add(tableControls, BorderLayout.SOUTH);

        // Right Side: Summary Card
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        JPanel summaryCard = Theme.createShadowCardPanel(Theme.BG_CARD);
        summaryCard.setPreferredSize(new Dimension(330, 430));
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));

        JLabel summaryTitle = new JLabel("📋  Order Summary");
        summaryTitle.setFont(Theme.FONT_TITLE);
        summaryTitle.setForeground(Theme.PRIMARY_DARK);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtotalLabel = new JLabel("Subtotal: $0.00");
        subtotalLabel.setFont(Theme.FONT_BODY);
        subtotalLabel.setForeground(Theme.TEXT_DARK);
        subtotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        discountLabel = new JLabel("Discount: $0.00");
        discountLabel.setFont(Theme.FONT_BODY);
        discountLabel.setForeground(Theme.STATUS_DELIVERED);
        discountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(Theme.FONT_SUBTITLE);
        totalLabel.setForeground(Theme.PRIMARY_DARK);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Promo Code panel
        JLabel promoLabel = new JLabel("Have a Promo Code?");
        promoLabel.setFont(Theme.FONT_SMALL_ITALIC);
        promoLabel.setForeground(Theme.GOLD_ACCENT);
        promoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        promoField = Theme.createStyledTextField("Enter Promo Code");
        promoField.setMaximumSize(new Dimension(290, 38));
        promoField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton applyPromoBtn = Theme.createRoundedButton("Apply Code", Theme.PRIMARY_DARK, Theme.TEXT_LIGHT);
        applyPromoBtn.setMaximumSize(new Dimension(290, 38));
        applyPromoBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyPromoBtn.addActionListener(e -> applyPromoCode());

        JButton checkoutBtn = Theme.createGradientButton("✅ Proceed to Checkout", Theme.STATUS_DELIVERED, new Color(0x2E, 0x7D, 0x32), Theme.TEXT_LIGHT);
        checkoutBtn.setMaximumSize(new Dimension(290, 45));
        checkoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkoutBtn.addActionListener(e -> handleCheckout());

        summaryCard.add(Box.createVerticalStrut(5));
        summaryCard.add(summaryTitle);
        summaryCard.add(Box.createVerticalStrut(20));
        summaryCard.add(subtotalLabel);
        summaryCard.add(Box.createVerticalStrut(10));
        summaryCard.add(discountLabel);
        summaryCard.add(Box.createVerticalStrut(15));
        summaryCard.add(totalLabel);
        summaryCard.add(Box.createVerticalStrut(25));
        summaryCard.add(new JSeparator());
        summaryCard.add(Box.createVerticalStrut(15));
        summaryCard.add(promoLabel);
        summaryCard.add(Box.createVerticalStrut(5));
        summaryCard.add(promoField);
        summaryCard.add(Box.createVerticalStrut(10));
        summaryCard.add(applyPromoBtn);
        summaryCard.add(Box.createVerticalStrut(25));
        summaryCard.add(checkoutBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        rightPanel.add(summaryCard, gbc);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double subtotal = 0;

        for (CartItem ci : cart) {
            Object[] row = {
                    ci.item.getItemName(),
                    "$" + String.format("%.2f", ci.item.getPrice()),
                    ci.quantity,
                    "$" + String.format("%.2f", ci.getSubtotal()),
                    "Remove"
            };
            cartTableModel.addRow(row);
            subtotal += ci.getSubtotal();
        }

        double discount = (subtotal * discountRate) + flatDiscount;
        if (discount > subtotal) discount = subtotal;
        double finalTotal = subtotal - discount;

        subtotalLabel.setText("Subtotal: $" + String.format("%.2f", subtotal));
        discountLabel.setText("Discount: -$" + String.format("%.2f", discount) + (activeDiscountCode.isEmpty() ? "" : " (" + activeDiscountCode + ")"));
        totalLabel.setText("Total: $" + String.format("%.2f", finalTotal));
    }

    private void removeSelectedCartItem() {
        int selected = cartTable.getSelectedRow();
        if (selected >= 0) {
            cart.remove(selected);
            if (navCartBtn != null) {
                navCartBtn.setText("🛒 Cart (" + cart.size() + ")");
            }
            refreshCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearCart() {
        cart.clear();
        if (navCartBtn != null) {
            navCartBtn.setText("🛒 Cart (0)");
        }
        activeDiscountCode = "";
        discountRate = 0.0;
        flatDiscount = 0.0;
        promoField.setText("Enter Promo Code");
        refreshCartTable();
    }

    private void applyPromoCode() {
        String code = promoField.getText().trim().toUpperCase();
        double subtotal = 0;
        for (CartItem ci : cart) {
            subtotal += ci.getSubtotal();
        }

        if (subtotal == 0) {
            JOptionPane.showMessageDialog(this, "Please add items to your cart first.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (code.equals("COFFEE10")) {
            discountRate = 0.10;
            flatDiscount = 0.0;
            activeDiscountCode = "COFFEE10";
            JOptionPane.showMessageDialog(this, "Promo code COFFEE10 applied! 10% off your entire order.", "Promo Code Applied", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.equals("WELCOME5")) {
            discountRate = 0.0;
            flatDiscount = 5.0;
            activeDiscountCode = "WELCOME5";
            JOptionPane.showMessageDialog(this, "Promo code WELCOME5 applied! $5.00 off your entire order.", "Promo Code Applied", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.isEmpty() || code.equals("ENTER PROMO CODE")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid promo code.", "Invalid Code", JOptionPane.WARNING_MESSAGE);
            return;
        } else {
            JOptionPane.showMessageDialog(this, "Invalid code. Try 'COFFEE10' or 'WELCOME5'.", "Promo Code Failed", JOptionPane.ERROR_MESSAGE);
            discountRate = 0.0;
            flatDiscount = 0.0;
            activeDiscountCode = "";
        }

        refreshCartTable();
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add items from the Coffee Menu.", "Checkout Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double subtotal = 0;
        for (CartItem ci : cart) {
            subtotal += ci.getSubtotal();
        }
        double discount = (subtotal * discountRate) + flatDiscount;
        if (discount > subtotal) discount = subtotal;
        double finalTotal = subtotal - discount;

        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to checkout and place this order?\nTotal Amount: $" + String.format("%.2f", finalTotal),
                "Confirm Order", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            boolean allPlaced = true;

            for (CartItem ci : cart) {
                double itemOriginal = ci.getSubtotal();
                double itemProportional = itemOriginal / subtotal;
                double itemFinal = itemOriginal - (discount * itemProportional);

                for (int q = 0; q < ci.quantity; q++) {
                    double singleFinalPrice = itemFinal / ci.quantity;
                    boolean res = DatabaseManager.placeOrder(
                            currentUser.getId(),
                            ci.item.getId(),
                            "Pending",
                            activeDiscountCode.isEmpty() ? null : activeDiscountCode,
                            singleFinalPrice
                    );
                    if (!res) allPlaced = false;
                }
            }

            if (allPlaced) {
                JOptionPane.showMessageDialog(this, "Order placed successfully! Brewing in progress...", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearCart();
                showPage(PAGE_HISTORY);
            } else {
                JOptionPane.showMessageDialog(this, "Some items failed to place. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    // HISTORY TAB CREATION
    // ============================================================
    private JPanel createHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_SECTION);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📋  Your Order History");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] cols = {"Order ID", "Date", "Item Name", "Original Price", "Paid Price", "Status"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        historyTable = new JTable(historyTableModel);
        Theme.setupTable(historyTable);

        // Custom renderer for Status badges
        historyTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                String val = (String) value;
                if (val != null) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(Theme.FONT_BODY_BOLD);
                    if (val.equalsIgnoreCase("Pending")) {
                        setForeground(Theme.STATUS_PENDING);
                    } else if (val.equalsIgnoreCase("Preparing")) {
                        setForeground(Theme.STATUS_PREPARING);
                    } else if (val.equalsIgnoreCase("Delivered")) {
                        setForeground(Theme.STATUS_DELIVERED);
                    }
                }
                return c;
            }
        });

        JPanel bottomControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomControls.setOpaque(false);

        JButton refreshBtn = Theme.createGradientButton("🔄 Refresh Orders", Theme.PRIMARY_DARK, Theme.PRIMARY_DARK_ALT, Theme.TEXT_LIGHT);
        refreshBtn.addActionListener(e -> loadOrderHistory());
        bottomControls.add(refreshBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        panel.add(bottomControls, BorderLayout.SOUTH);

        return panel;
    }

    private void loadOrderHistory() {
        historyTableModel.setRowCount(0);
        List<Order> orders = DatabaseManager.getCustomerOrders(currentUser.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Order o : orders) {
            Object[] row = {
                    "#" + o.getId(),
                    o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "N/A",
                    o.getItemName(),
                    "$" + String.format("%.2f", o.getItemPrice()),
                    "$" + String.format("%.2f", o.getFinalPrice()),
                    o.getStatus()
            };
            historyTableModel.addRow(row);
        }
    }

    // ============================================================
    // FEEDBACK TAB CREATION
    // ============================================================
    private JPanel createFeedbackTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_SECTION);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = Theme.createShadowCardPanel(Theme.BG_CARD);
        formPanel.setPreferredSize(new Dimension(520, 480));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("💬  We Value Your Feedback!");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Let us know about your coffee experience");
        subtitle.setFont(Theme.FONT_SMALL_ITALIC);
        subtitle.setForeground(Theme.GOLD_ACCENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ratingLabel = new JLabel("Rating (1 - 5 Stars):");
        ratingLabel.setFont(Theme.FONT_BODY_BOLD);
        ratingLabel.setForeground(Theme.TEXT_DARK);
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> ratingCombo = new JComboBox<>(new String[]{
                "⭐⭐⭐⭐⭐ (5 - Excellent)",
                "⭐⭐⭐⭐ (4 - Very Good)",
                "⭐⭐⭐ (3 - Good)",
                "⭐⭐ (2 - Average)",
                "⭐ (1 - Poor)"
        });
        ratingCombo.setFont(Theme.FONT_BODY);
        ratingCombo.setMaximumSize(new Dimension(350, 40));
        ratingCombo.setBackground(Theme.BG_WHITE);
        ratingCombo.setForeground(Theme.TEXT_DARK);
        ratingCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel commentLabel = new JLabel("Tell us more:");
        commentLabel.setFont(Theme.FONT_BODY_BOLD);
        commentLabel.setForeground(Theme.TEXT_DARK);
        commentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea commentArea = new JTextArea(5, 20);
        commentArea.setFont(Theme.FONT_BODY);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);

        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setMaximumSize(new Dimension(420, 150));
        commentScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR));
        commentScroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton submitBtn = Theme.createGradientButton("📩 Submit Feedback", Theme.PRIMARY_DARK, Theme.PRIMARY_DARK_ALT, Theme.TEXT_LIGHT);
        submitBtn.setMaximumSize(new Dimension(420, 45));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitBtn.addActionListener(e -> {
            int selectedIndex = ratingCombo.getSelectedIndex();
            int rating = 5 - selectedIndex;
            String comments = commentArea.getText().trim();

            if (comments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please write a brief comment.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = DatabaseManager.submitFeedback(currentUser.getId(), rating, comments);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thank you for your feedback! Have a great day!", "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
                ratingCombo.setSelectedIndex(0);
                commentArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit feedback. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(ratingLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(ratingCombo);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(commentLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(commentScroll);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(submitBtn);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(formPanel);
        panel.add(wrap, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // LOGOUT & CLOSE LOGIC
    // ============================================================
    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginRegisterWindow().setVisible(true));
        }
    }
}
