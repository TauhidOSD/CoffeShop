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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private final User currentAdmin;
    private JTabbedPane tabbedPane;
    
    // Manage Menu Tab Components
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JTextField itemNameField;
    private JTextField itemPriceField;
    private JButton addItemBtn;
    private JButton updateItemBtn;
    private JButton deleteItemBtn;
    private int selectedMenuId = -1;
    
    // Manage Orders Tab Components
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    
    // Sales Report Tab Components
    private JLabel totalRevenueLabel;
    private JLabel totalOrdersLabel;
    private JLabel avgOrderLabel;
    private JLabel popularItemLabel;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    
    // Feedback Tab Components
    private JTable feedbackTable;
    private DefaultTableModel feedbackTableModel;

    public AdminDashboard(User admin) {
        this.currentAdmin = admin;
        
        setTitle("Coffee Shop Admin Portal - " + admin.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        
        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.PRIMARY_LIGHT);
        
        // 1. Premium Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_DARK);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel logoLabel = new JLabel("BREW & BLEND ADMIN CONSOLE", SwingConstants.LEFT);
        logoLabel.setFont(Theme.FONT_TITLE);
        logoLabel.setForeground(Theme.TEXT_LIGHT);
        
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userInfoPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Administrator: " + admin.getName(), SwingConstants.RIGHT);
        welcomeLabel.setFont(Theme.FONT_BODY);
        welcomeLabel.setForeground(Theme.PRIMARY_LIGHT);
        
        JButton logoutBtn = Theme.createRoundedButton("Logout", Theme.PRIMARY_ACCENT, Theme.TEXT_LIGHT);
        logoutBtn.setPreferredSize(new Dimension(90, 30));
        logoutBtn.addActionListener(e -> handleLogout());
        
        userInfoPanel.add(welcomeLabel);
        userInfoPanel.add(logoutBtn);
        
        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(userInfoPanel, BorderLayout.EAST);
        
        // 2. Main Tabbed Layout
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_SUBTITLE);
        tabbedPane.setBackground(Theme.BG_CARD);
        tabbedPane.setForeground(Theme.PRIMARY_DARK);
        
        tabbedPane.addTab("Manage Menu", createMenuTab());
        tabbedPane.addTab("Manage Orders", createOrdersTab());
        tabbedPane.addTab("Sales Reports", createReportsTab());
        tabbedPane.addTab("Customer Feedback", createFeedbackTab());
        
        // Refresh tables on tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) {
                loadMenuData();
            } else if (index == 1) {
                loadOrdersData();
            } else if (index == 2) {
                loadReportData();
            } else if (index == 3) {
                loadFeedbackData();
            }
        });
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainContainer);
        
        // Load default tab
        loadMenuData();
    }
    
    // ==========================================
    // MANAGE MENU TAB
    // ==========================================
    private JPanel createMenuTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Theme.PRIMARY_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Left Side: List Table
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        
        String[] cols = {"Item ID", "Item Name", "Price"};
        menuTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        menuTable = new JTable(menuTableModel);
        Theme.setupTable(menuTable);
        
        // Selection Listener
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            int selected = menuTable.getSelectedRow();
            if (selected >= 0) {
                selectedMenuId = (Integer) menuTable.getValueAt(selected, 0);
                String name = (String) menuTable.getValueAt(selected, 1);
                String priceStr = ((String) menuTable.getValueAt(selected, 2)).replace("$", "");
                
                itemNameField.setText(name);
                itemPriceField.setText(priceStr);
                
                updateItemBtn.setEnabled(true);
                deleteItemBtn.setEnabled(true);
            }
        });
        
        listPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        
        // Right Side: Form Panel
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);
        
        JPanel card = Theme.createCardPanel(Theme.BG_CARD);
        card.setPreferredSize(new Dimension(320, 420));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        JLabel formTitle = new JLabel("Coffee Menu Editor");
        formTitle.setFont(Theme.FONT_TITLE);
        formTitle.setForeground(Theme.PRIMARY_DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel("Coffee/Item Name:");
        nameLabel.setFont(Theme.FONT_BODY_BOLD);
        nameLabel.setForeground(Theme.TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        itemNameField = Theme.createStyledTextField("e.g. Flat White");
        itemNameField.setMaximumSize(new Dimension(280, 40));
        itemNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("Price ($):");
        priceLabel.setFont(Theme.FONT_BODY_BOLD);
        priceLabel.setForeground(Theme.TEXT_DARK);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        itemPriceField = Theme.createStyledTextField("e.g. 3.75");
        itemPriceField.setMaximumSize(new Dimension(280, 40));
        itemPriceField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        addItemBtn = Theme.createRoundedButton("Add New Item", Theme.PRIMARY_DARK, Theme.TEXT_LIGHT);
        addItemBtn.setMaximumSize(new Dimension(280, 40));
        addItemBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addItemBtn.addActionListener(e -> handleAddMenuItem());
        
        updateItemBtn = Theme.createRoundedButton("Save Changes", Theme.PRIMARY_ACCENT, Theme.TEXT_LIGHT);
        updateItemBtn.setMaximumSize(new Dimension(280, 40));
        updateItemBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateItemBtn.setEnabled(false);
        updateItemBtn.addActionListener(e -> handleUpdateMenuItem());
        
        deleteItemBtn = Theme.createRoundedButton("Delete Selected Item", new Color(0xD3, 0x2F, 0x2F), Theme.TEXT_LIGHT);
        deleteItemBtn.setMaximumSize(new Dimension(280, 40));
        deleteItemBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteItemBtn.setEnabled(false);
        deleteItemBtn.addActionListener(e -> handleDeleteMenuItem());
        
        JButton clearBtn = Theme.createRoundedButton("Clear Form Selection", Theme.BORDER_COLOR, Theme.TEXT_DARK);
        clearBtn.setMaximumSize(new Dimension(280, 35));
        clearBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearBtn.addActionListener(e -> resetMenuForm());
        
        card.add(Box.createVerticalStrut(10));
        card.add(formTitle);
        card.add(Box.createVerticalStrut(20));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(itemNameField);
        card.add(Box.createVerticalStrut(15));
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(itemPriceField);
        card.add(Box.createVerticalStrut(25));
        card.add(addItemBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(updateItemBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(deleteItemBtn);
        card.add(Box.createVerticalStrut(15));
        card.add(clearBtn);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        formWrapper.add(card, gbc);
        
        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(formWrapper, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadMenuData() {
        menuTableModel.setRowCount(0);
        List<MenuItem> list = DatabaseManager.getMenuItems();
        for (MenuItem item : list) {
            Object[] row = {
                    item.getId(),
                    item.getItemName(),
                    "$" + String.format("%.2f", item.getPrice())
            };
            menuTableModel.addRow(row);
        }
    }
    
    private void resetMenuForm() {
        selectedMenuId = -1;
        itemNameField.setText("");
        itemPriceField.setText("");
        updateItemBtn.setEnabled(false);
        deleteItemBtn.setEnabled(false);
        addItemBtn.setEnabled(true);
        menuTable.clearSelection();
    }
    
    private void handleAddMenuItem() {
        String name = itemNameField.getText().trim();
        String priceStr = itemPriceField.getText().trim();
        
        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item name and price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
            
            boolean success = DatabaseManager.addMenuItem(name, price);
            if (success) {
                JOptionPane.showMessageDialog(this, "Menu item '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetMenuForm();
                loadMenuData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add menu item. Name may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive decimal price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateMenuItem() {
        if (selectedMenuId == -1) return;
        
        String name = itemNameField.getText().trim();
        String priceStr = itemPriceField.getText().trim();
        
        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item name and price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
            
            boolean success = DatabaseManager.updateMenuItem(selectedMenuId, name, price);
            if (success) {
                JOptionPane.showMessageDialog(this, "Menu item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetMenuForm();
                loadMenuData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update menu item. Name may conflict with an existing item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive decimal price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteMenuItem() {
        if (selectedMenuId == -1) return;
        
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this menu item? This will also remove any orders associated with it.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            boolean success = DatabaseManager.deleteMenuItem(selectedMenuId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Menu item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetMenuForm();
                loadMenuData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete menu item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ==========================================
    // MANAGE ORDERS TAB
    // ==========================================
    private JPanel createOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.PRIMARY_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("Customer Order Administration");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        String[] cols = {"Order ID", "Date", "Customer", "Item Name", "Original Price", "Paid Price", "Promo", "Status"};
        ordersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        ordersTable = new JTable(ordersTableModel);
        Theme.setupTable(ordersTable);
        
        // Status Column Renderer
        ordersTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
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
        
        // Actions Bottom Panel
        JPanel actionControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionControls.setOpaque(false);
        
        JButton preparingBtn = Theme.createRoundedButton("Mark Preparing", Theme.STATUS_PREPARING, Theme.TEXT_LIGHT);
        preparingBtn.addActionListener(e -> updateStatusOfSelected("Preparing"));
        
        JButton deliveredBtn = Theme.createRoundedButton("Mark Delivered", Theme.STATUS_DELIVERED, Theme.TEXT_LIGHT);
        deliveredBtn.addActionListener(e -> updateStatusOfSelected("Delivered"));
        
        JButton refreshBtn = Theme.createRoundedButton("Refresh List", Theme.PRIMARY_DARK, Theme.TEXT_LIGHT);
        refreshBtn.addActionListener(e -> loadOrdersData());
        
        actionControls.add(preparingBtn);
        actionControls.add(deliveredBtn);
        actionControls.add(refreshBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        panel.add(actionControls, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadOrdersData() {
        ordersTableModel.setRowCount(0);
        List<Order> orders = DatabaseManager.getAllOrders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Order o : orders) {
            Object[] row = {
                    "#" + o.getId(),
                    o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "N/A",
                    o.getUserName(),
                    o.getItemName(),
                    "$" + String.format("%.2f", o.getItemPrice()),
                    "$" + String.format("%.2f", o.getFinalPrice()),
                    o.getDiscountCode() != null ? o.getDiscountCode() : "-",
                    o.getStatus()
            };
            ordersTableModel.addRow(row);
        }
    }
    
    private void updateStatusOfSelected(String status) {
        int selected = ordersTable.getSelectedRow();
        if (selected >= 0) {
            String idStr = ((String) ordersTable.getValueAt(selected, 0)).replace("#", "");
            int orderId = Integer.parseInt(idStr);
            boolean success = DatabaseManager.updateOrderStatus(orderId, status);
            if (success) {
                JOptionPane.showMessageDialog(this, "Order #" + orderId + " updated to " + status + ".", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadOrdersData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order from the list.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // ==========================================
    // SALES REPORTS TAB
    // ==========================================
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Theme.PRIMARY_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 1. Grid of KPI cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        totalRevenueLabel = new JLabel("$0.00", SwingConstants.CENTER);
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalRevenueLabel.setForeground(Theme.STATUS_DELIVERED);
        JPanel revCard = createKpiCard("TOTAL SALES REVENUE", totalRevenueLabel);
        
        totalOrdersLabel = new JLabel("0", SwingConstants.CENTER);
        totalOrdersLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalOrdersLabel.setForeground(Theme.PRIMARY_DARK);
        JPanel ordCard = createKpiCard("TOTAL ORDERS PLACED", totalOrdersLabel);
        
        avgOrderLabel = new JLabel("$0.00", SwingConstants.CENTER);
        avgOrderLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        avgOrderLabel.setForeground(Theme.PRIMARY_ACCENT);
        JPanel avgCard = createKpiCard("AVERAGE ORDER VALUE", avgOrderLabel);
        
        popularItemLabel = new JLabel("None", SwingConstants.CENTER);
        popularItemLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        popularItemLabel.setForeground(new Color(103, 58, 183)); // Purple accent
        JPanel popCard = createKpiCard("POPULAR COFFEE", popularItemLabel);
        
        statsPanel.add(revCard);
        statsPanel.add(ordCard);
        statsPanel.add(avgCard);
        statsPanel.add(popCard);
        
        // 2. Table showing Sales by Item
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        
        JLabel tableTitle = new JLabel("Coffee Sales breakdown by Menu Item");
        tableTitle.setFont(Theme.FONT_SUBTITLE);
        tableTitle.setForeground(Theme.PRIMARY_DARK);
        tableTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        String[] cols = {"Coffee Item", "Unit Price", "Quantity Sold", "Total Revenue Generated"};
        reportTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable = new JTable(reportTableModel);
        Theme.setupTable(reportTable);
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton refreshBtn = Theme.createRoundedButton("Refresh Report Data", Theme.PRIMARY_DARK, Theme.TEXT_LIGHT);
        refreshBtn.addActionListener(e -> loadReportData());
        footer.add(refreshBtn);
        
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createKpiCard(String titleText, JLabel valueLabel) {
        JPanel card = Theme.createCardPanel(Theme.BG_CARD);
        card.setLayout(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(220, 100));
        
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(Theme.FONT_SMALL_ITALIC);
        title.setForeground(Theme.PRIMARY_ACCENT);
        
        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    @SuppressWarnings("unchecked")
    private void loadReportData() {
        Map<String, Object> data = DatabaseManager.getSalesReportData();
        
        double totalSales = (Double) data.get("totalSales");
        int totalOrders = (Integer) data.get("totalOrders");
        double avgVal = (Double) data.get("avgOrderValue");
        String pop = (String) data.get("popularItem");
        
        totalRevenueLabel.setText("$" + String.format("%.2f", totalSales));
        totalOrdersLabel.setText(String.valueOf(totalOrders));
        avgOrderLabel.setText("$" + String.format("%.2f", avgVal));
        popularItemLabel.setText(pop);
        
        // Populate report table
        reportTableModel.setRowCount(0);
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("breakdown");
        for (Map<String, Object> item : list) {
            Object[] row = {
                    item.get("name"),
                    "$" + String.format("%.2f", (Double) item.get("price")),
                    item.get("quantity"),
                    "$" + String.format("%.2f", (Double) item.get("revenue"))
            };
            reportTableModel.addRow(row);
        }
    }
    
    // ==========================================
    // CUSTOMER FEEDBACK TAB
    // ==========================================
    private JPanel createFeedbackTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.PRIMARY_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("Customer Reviews & Feedback");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        String[] cols = {"Date", "Customer Name", "Rating", "Comments"};
        feedbackTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        feedbackTable = new JTable(feedbackTableModel);
        Theme.setupTable(feedbackTable);
        
        // Custom renderer for Rating Stars
        feedbackTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(new Color(255, 193, 7)); // Gold Star Color
                setFont(Theme.FONT_BODY_BOLD);
                return c;
            }
        });
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton refreshBtn = Theme.createRoundedButton("Refresh Feedback", Theme.PRIMARY_DARK, Theme.TEXT_LIGHT);
        refreshBtn.addActionListener(e -> loadFeedbackData());
        footer.add(refreshBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(feedbackTable), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadFeedbackData() {
        feedbackTableModel.setRowCount(0);
        List<Feedback> list = DatabaseManager.getAllFeedback();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Feedback f : list) {
            StringBuilder stars = new StringBuilder();
            for (int s = 0; s < f.getRating(); s++) {
                stars.append("⭐");
            }
            Object[] row = {
                    f.getFeedbackDate() != null ? sdf.format(f.getFeedbackDate()) : "N/A",
                    f.getUserName(),
                    stars.toString(),
                    f.getComments()
            };
            feedbackTableModel.addRow(row);
        }
    }
    
    // ==========================================
    // LOGOUT & CLOSE LOGIC
    // ==========================================
    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginRegisterWindow().setVisible(true));
        }
    }
}
