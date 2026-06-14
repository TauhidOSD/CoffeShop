package com.coffeeshop.database;

import com.coffeeshop.model.Feedback;
import com.coffeeshop.model.MenuItem;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL_NO_DB = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "coffeeshop_db";
    private static final String DB_URL = DB_URL_NO_DB + DB_NAME;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";

    static {
        try {
            // Explicitly load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please make sure the connector JAR is in the classpath.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() {
        // First connect without DB to create database if it doesn't exist
        try (Connection conn = DriverManager.getConnection(DB_URL_NO_DB, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database '" + DB_NAME + "' ensured.");
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Now connect to the database to create tables
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Create Users Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "role VARCHAR(20) DEFAULT 'customer'" +
                    ")");

            // 2. Create Menu Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS menu (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "item_name VARCHAR(100) NOT NULL UNIQUE," +
                    "price DECIMAL(10, 2) NOT NULL" +
                    ")");

            // 3. Create Orders Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "item_id INT NOT NULL," +
                    "status VARCHAR(50) DEFAULT 'Pending'," +
                    "discount_code VARCHAR(20) DEFAULT NULL," +
                    "final_price DECIMAL(10, 2) NOT NULL," +
                    "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (item_id) REFERENCES menu(id) ON DELETE CASCADE" +
                    ")");

            // 4. Create Feedback Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS feedback (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "rating INT NOT NULL," +
                    "comments TEXT," +
                    "feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")");

            System.out.println("All tables verified/created successfully.");

            // 5. Seed default data if empty
            seedInitialData(conn);

        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Connection conn) throws SQLException {
        // Check users count
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Seed admin
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, "Admin User");
                    ps.setString(2, "admin@coffeeshop.com");
                    ps.setString(3, "admin123");
                    ps.setString(4, "admin");
                    ps.executeUpdate();
                }
                // Seed a customer for convenience
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, "John Doe");
                    ps.setString(2, "john@gmail.com");
                    ps.setString(3, "john123");
                    ps.setString(4, "customer");
                    ps.executeUpdate();
                }
                System.out.println("Seeded default users.");
            }
        }

        // Check menu count
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM menu")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String[][] items = {
                        {"Espresso", "2.50"},
                        {"Cappuccino", "3.50"},
                        {"Caffè Latte", "3.80"},
                        {"Caramel Macchiato", "4.20"},
                        {"Americano", "3.00"},
                        {"Mocha", "4.50"},
                        {"Cold Brew", "3.50"},
                        {"Chocolate Croissant", "3.00"},
                        {"Blueberry Muffin", "2.80"}
                };
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO menu (item_name, price) VALUES (?, ?)")) {
                    for (String[] item : items) {
                        ps.setString(1, item[0]);
                        ps.setDouble(2, Double.parseDouble(item[1]));
                        ps.executeUpdate();
                    }
                }
                System.out.println("Seeded default menu items.");
            }
        }
    }

    // ==========================================
    // USER OPERATIONS
    // ==========================================

    public static boolean registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email.toLowerCase().trim());
            ps.setString(3, password);
            ps.setString(4, role.toLowerCase().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase().trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    // ==========================================
    // MENU OPERATIONS
    // ==========================================

    public static List<MenuItem> getMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu ORDER BY item_name";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch menu items: " + e.getMessage());
        }
        return list;
    }

    public static List<MenuItem> searchMenuItems(String query) {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE item_name LIKE ? ORDER BY item_name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MenuItem(
                            rs.getInt("id"),
                            rs.getString("item_name"),
                            rs.getDouble("price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to search menu items: " + e.getMessage());
        }
        return list;
    }

    public static boolean addMenuItem(String name, double price) {
        String sql = "INSERT INTO menu (item_name, price) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to add menu item: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateMenuItem(int id, String name, double price) {
        String sql = "UPDATE menu SET item_name = ?, price = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update menu item: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteMenuItem(int id) {
        String sql = "DELETE FROM menu WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete menu item: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // ORDER OPERATIONS
    // ==========================================

    public static boolean placeOrder(int userId, int itemId, String status, String discountCode, double finalPrice) {
        String sql = "INSERT INTO orders (user_id, item_id, status, discount_code, final_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            ps.setString(3, status);
            if (discountCode == null || discountCode.trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, discountCode);
            }
            ps.setDouble(5, finalPrice);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to place order: " + e.getMessage());
            return false;
        }
    }

    public static List<Order> getCustomerOrders(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, m.item_name, m.price as item_price " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN menu m ON o.item_id = m.id " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.order_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("user_name"),
                            rs.getInt("item_id"),
                            rs.getString("item_name"),
                            rs.getDouble("item_price"),
                            rs.getString("status"),
                            rs.getString("discount_code"),
                            rs.getDouble("final_price"),
                            rs.getTimestamp("order_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch customer orders: " + e.getMessage());
        }
        return list;
    }

    public static List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, m.item_name, m.price as item_price " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN menu m ON o.item_id = m.id " +
                "ORDER BY o.order_date DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getDouble("item_price"),
                        rs.getString("status"),
                        rs.getString("discount_code"),
                        rs.getDouble("final_price"),
                        rs.getTimestamp("order_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch all orders: " + e.getMessage());
        }
        return list;
    }

    public static boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update order status: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // FEEDBACK OPERATIONS
    // ==========================================

    public static boolean submitFeedback(int userId, int rating, String comments) {
        String sql = "INSERT INTO feedback (user_id, rating, comments) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, rating);
            ps.setString(3, comments);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to submit feedback: " + e.getMessage());
            return false;
        }
    }

    public static List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.*, u.name as user_name " +
                "FROM feedback f " +
                "JOIN users u ON f.user_id = u.id " +
                "ORDER BY f.feedback_date DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Feedback(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getInt("rating"),
                        rs.getString("comments"),
                        rs.getTimestamp("feedback_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch feedback: " + e.getMessage());
        }
        return list;
    }

    // ==========================================
    // SALES REPORT ANALYSIS
    // ==========================================

    public static Map<String, Object> getSalesReportData() {
        Map<String, Object> report = new HashMap<>();
        double totalSales = 0.0;
        int totalOrders = 0;
        double avgOrderValue = 0.0;
        String popularItem = "None";
        List<Map<String, Object>> itemBreakdown = new ArrayList<>();

        String summarySql = "SELECT COUNT(*) as total_count, SUM(final_price) as total_sum, AVG(final_price) as avg_val FROM orders";
        String popularItemSql = "SELECT m.item_name, COUNT(*) as count_sold " +
                "FROM orders o " +
                "JOIN menu m ON o.item_id = m.id " +
                "GROUP BY o.item_id, m.item_name " +
                "ORDER BY count_sold DESC " +
                "LIMIT 1";
        String itemizedSql = "SELECT m.item_name, m.price, COUNT(*) as qty, SUM(o.final_price) as total_revenue " +
                "FROM orders o " +
                "JOIN menu m ON o.item_id = m.id " +
                "GROUP BY o.item_id, m.item_name, m.price " +
                "ORDER BY total_revenue DESC";

        try (Connection conn = getConnection()) {
            // Summary stats
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(summarySql)) {
                if (rs.next()) {
                    totalOrders = rs.getInt("total_count");
                    totalSales = rs.getDouble("total_sum");
                    avgOrderValue = rs.getDouble("avg_val");
                }
            }

            // Popular item
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(popularItemSql)) {
                if (rs.next()) {
                    popularItem = rs.getString("item_name") + " (" + rs.getInt("count_sold") + " sold)";
                }
            }

            // Itemized breakdown
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(itemizedSql)) {
                while (rs.next()) {
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("name", rs.getString("item_name"));
                    itemData.put("price", rs.getDouble("price"));
                    itemData.put("quantity", rs.getInt("qty"));
                    itemData.put("revenue", rs.getDouble("total_revenue"));
                    itemBreakdown.add(itemData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to build sales report: " + e.getMessage());
        }

        report.put("totalSales", totalSales);
        report.put("totalOrders", totalOrders);
        report.put("avgOrderValue", avgOrderValue);
        report.put("popularItem", popularItem);
        report.put("breakdown", itemBreakdown);

        return report;
    }
}
