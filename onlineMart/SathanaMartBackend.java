import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class SathanaMartBackend {

    // =========================================
    // DATABASE CONFIGURATION
    // =========================================

    // Reads SATHANAMART_* first, then Railway's own MYSQL* variables,
    // then falls back to a local default (for running on your laptop).
    private static String env(String first, String second, String fallback) {
        String v = System.getenv(first);
        if (v != null && !v.isBlank()) return v;
        v = System.getenv(second);
        if (v != null && !v.isBlank()) return v;
        return fallback;
    }

    private static final String DB_HOST =
            env("SATHANAMART_DB_HOST", "MYSQLHOST", "localhost");

    private static final String DB_PORT =
            env("SATHANAMART_DB_PORT", "MYSQLPORT", "3306");

    private static final String DB_NAME =
            env("SATHANAMART_DB_NAME", "MYSQLDATABASE", "SathanaMart");

    private static final String USERNAME =
            env("SATHANAMART_DB_USER", "MYSQLUSER", "root");

    // Never hard-code the password. Set it as an environment variable.
    private static final String PASSWORD =
            env("SATHANAMART_DB_PASSWORD", "MYSQLPASSWORD", "");

    // PREFERRED = use SSL when the server offers it, otherwise plain.
    private static final String SSL_MODE =
            env("SATHANAMART_DB_SSLMODE", "SATHANAMART_DB_SSLMODE", "PREFERRED");

    private static final String URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                    + "?sslMode=" + SSL_MODE
                    + "&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // =========================================
    // MAIN
    // =========================================

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Check database connection before starting.
            try (Connection con = getConnection()) {
                System.out.println("MySQL connection successful!");
            }

            initDatabase();

            int port = Integer.parseInt(
                    System.getenv().getOrDefault("PORT", "8080"));

            HttpServer server = HttpServer.create(
                    new InetSocketAddress("0.0.0.0", port), 0);

            // =====================================
            // FRONTEND FILE ROUTES
            // =====================================

            file(server, "/", "index.html", "text/html");
            file(server, "/index.html", "index.html", "text/html");
            file(server, "/script.js", "script.js",
                    "application/javascript");
            file(server, "/style.css", "style.css", "text/css");

            file(server, "/buyer.html", "buyer.html", "text/html");
            file(server, "/buyer.js", "buyer.js",
                    "application/javascript");
            file(server, "/buyer.css", "buyer.css", "text/css");

            file(server, "/seller.html", "seller.html", "text/html");
            file(server, "/seller.js", "seller.js",
                    "application/javascript");
            file(server, "/seller.css", "seller.css", "text/css");

            file(server, "/admin.html", "admin.html", "text/html");
            file(server, "/admin.js", "admin.js",
                    "application/javascript");
            file(server, "/admin.css", "admin.css", "text/css");

            file(server, "/cart.html", "cart.html", "text/html");
            file(server, "/cart.js", "cart.js",
                    "application/javascript");
            file(server, "/cart.css", "cart.css", "text/css");

            file(server, "/wishlist.html", "wishlist.html", "text/html");
            file(server, "/wishlist.js", "wishlist.js",
                    "application/javascript");
            file(server, "/wishlist.css", "wishlist.css", "text/css");

            file(server, "/orders.html", "orders.html", "text/html");
            file(server, "/orders.js", "orders.js",
                    "application/javascript");
            file(server, "/orders.css", "orders.css", "text/css");

            // =====================================
            // REGISTER
            // =====================================

            server.createContext("/register", exchange -> {
                if (!method(exchange, "POST")) return;

                String data = body(exchange);
                String name = value(data, "name");
                String email = value(data, "email");
                String password = value(data, "password");
                String role = value(data, "role");

                role = role.trim().toLowerCase();

                if (!role.equals("buyer") && !role.equals("seller")) {
                   send(exchange, 403, "Only Buyer or Seller accounts are allowed.",
                     "text/plain; charset=UTF-8");
                      return;
                }

                String sql =
                        "INSERT INTO users (name, email, password, role) "
                        + "VALUES (?, ?, ?, ?)";

                try (Connection con = getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, password);
                    ps.setString(4, role);

                    if (ps.executeUpdate() > 0) {
                        text(exchange, "Account Created Successfully!");
                    } else {
                        text(exchange, "Account Creation Failed!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    text(exchange, "Account Creation Failed!");
                }
            });

            // =====================================
            // LOGIN
            // =====================================
            server.createContext("/login", exchange -> {
    if (!method(exchange, "POST")) return;

    String data = body(exchange);
    String email = value(data, "email").trim();
    String password = value(data, "password");
    String role = value(data, "role").trim().toLowerCase();

    // Admin credentials are configured on the server.
    String adminEmail = System.getenv().getOrDefault(
            "SATHANAMART_ADMIN_EMAIL",
            "sathanasenthilkumar@gmail.com");

    String adminPassword =
            System.getenv("SATHANAMART_ADMIN_PASSWORD");

    if ("admin".equals(role)) {
        if (email.equalsIgnoreCase(adminEmail)
                && adminPassword != null
                && !adminPassword.isBlank()
                && password.equals(adminPassword)) {

            text(exchange, "Login Successfully! Welcome to SathanaMart");
        } else {
            send(exchange, 401, "Invalid Email, Password or Role!",
                    "text/plain; charset=UTF-8");
        }
        return;
    }

    // Public login is restricted to Buyer and Seller.
    if (!role.equals("buyer") && !role.equals("seller")) {
        send(exchange, 403, "Invalid account role.",
                "text/plain; charset=UTF-8");
        return;
    }

    String sql = "SELECT id FROM users "
            + "WHERE email=? AND password=? AND LOWER(role)=?";

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, email);
        ps.setString(2, password);
        ps.setString(3, role);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                text(exchange,
                        "Login Successfully! Welcome to SathanaMart");
            } else {
                send(exchange, 401, "Invalid Email, Password or Role!",
                        "text/plain; charset=UTF-8");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        send(exchange, 500, "Login Failed!",
                "text/plain; charset=UTF-8");
    }
});

            // =====================================
            // PRODUCTS: GET, POST, PUT, DELETE
            // =====================================

            server.createContext("/products", exchange -> {
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    getProducts(exchange);
                } else if ("POST".equalsIgnoreCase(method)) {
                    addProduct(exchange);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    updateProduct(exchange);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    deleteProduct(exchange);
                } else {
                    send(exchange, 405, "Method not allowed",
                            "text/plain; charset=UTF-8");
                }
            });

            // =====================================
            // ORDERS: GET, POST, PUT
            // =====================================

            server.createContext("/orders", exchange -> {
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    getOrders(exchange);
                } else if ("POST".equalsIgnoreCase(method)) {
                    addOrder(exchange);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    updateOrderStatus(exchange);
                } else {
                    send(exchange, 405, "Method not allowed",
                            "text/plain; charset=UTF-8");
                }
            });

            server.createContext("/reviews", exchange -> {
                  String method = exchange.getRequestMethod();

                  if ("POST".equalsIgnoreCase(method)) {
                      addProductReview(exchange);
                  } else {
                       send(exchange, 405, "Method not allowed",
                            "text/plain; charset=UTF-8");
                }
});

            // =====================================
            // USERS: GET, DELETE
            // =====================================

            server.createContext("/users", exchange -> {
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    getUsers(exchange);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    deleteUser(exchange);
                } else {
                    send(exchange, 405, "Method not allowed",
                            "text/plain; charset=UTF-8");
                }
            });

            // =====================================
            // START SERVER
            // =====================================

            server.start();

            System.out.println("SathanaMart server started!");
            System.out.println("Open: http://localhost:" + port);
            System.out.println("Products API: http://localhost:"
                    + port + "/products");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }

    // =========================================
    // DATABASE CONNECTION
    // =========================================

    private static void initDatabase() throws Exception {
        String[] statements = {
            "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "email VARCHAR(150) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "role VARCHAR(20) NOT NULL)",

            "CREATE TABLE IF NOT EXISTS products ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(150) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "stock INT NOT NULL DEFAULT 0, "
                + "category VARCHAR(100), "
                + "image LONGTEXT)",

            "CREATE TABLE IF NOT EXISTS orders ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "product_name VARCHAR(150) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "quantity INT NOT NULL, "
                + "total DOUBLE NOT NULL, "
                + "buyer_email VARCHAR(150) NOT NULL, "
                + "customer_name VARCHAR(100), "
                + "customer_phone VARCHAR(30), "
                + "customer_gender VARCHAR(20), "
                + "delivery_address VARCHAR(255), "
                + "delivery_city VARCHAR(100), "
                + "delivery_pincode VARCHAR(20), "
                + "payment_method VARCHAR(50), "
                + "status VARCHAR(30) NOT NULL DEFAULT 'Placed', "
                + "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

            "CREATE TABLE IF NOT EXISTS product_reviews ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "order_id INT NOT NULL, "
                + "buyer_email VARCHAR(150) NOT NULL, "
                + "product_name VARCHAR(150) NOT NULL, "
                + "rating INT NOT NULL, "
                + "review_comment TEXT, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection con = getConnection();
             java.sql.Statement st = con.createStatement()) {
            for (String sql : statements) {
                st.executeUpdate(sql);
            }
            System.out.println("Database tables are ready.");
        }
    }

    private static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // =========================================
    // GET PRODUCTS
    // =========================================

    private static void getProducts(HttpExchange ex) throws IOException {
        StringBuilder json = new StringBuilder("[");

        String sql =
                "SELECT id, name, price, stock, category, image "
                + "FROM products ORDER BY id DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"name\":").append(jsonString(rs.getString("name"))).append(",")
                    .append("\"price\":").append(rs.getDouble("price")).append(",")
                    .append("\"stock\":").append(rs.getInt("stock")).append(",")
                    .append("\"category\":").append(jsonString(rs.getString("category"))).append(",")
                    .append("\"image\":").append(jsonString(rs.getString("image")))
                    .append("}");
            }

            json.append("]");
            send(ex, 200, json.toString(), "application/json; charset=UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500,
                    "{\"error\":" + jsonString(e.getMessage()) + "}",
                    "application/json; charset=UTF-8");
        }
    }

    // =========================================
    // ADD PRODUCT
    // =========================================

    private static void addProduct(HttpExchange ex) throws IOException {
        String data = body(ex);

        String sql =
                "INSERT INTO products (name, price, stock, category, image) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value(data, "name"));
            ps.setDouble(2, Double.parseDouble(value(data, "price")));
            ps.setInt(3, Integer.parseInt(value(data, "stock")));
            ps.setString(4, value(data, "category"));
            ps.setString(5, value(data, "image"));

            text(ex, ps.executeUpdate() > 0
                    ? "Product Added Successfully!"
                    : "Product Addition Failed!");

        } catch (Exception e) {
            e.printStackTrace();
            text(ex, "Product Addition Failed!");
        }
    }

    // =========================================
    // UPDATE PRODUCT
    // =========================================

    private static void updateProduct(HttpExchange ex) throws IOException {
        String data = body(ex);

        String sql =
                "UPDATE products SET name=?, price=?, stock=?, "
                + "category=?, image=? WHERE id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value(data, "name"));
            ps.setDouble(2, Double.parseDouble(value(data, "price")));
            ps.setInt(3, Integer.parseInt(value(data, "stock")));
            ps.setString(4, value(data, "category"));
            ps.setString(5, value(data, "image"));
            ps.setInt(6, Integer.parseInt(value(data, "id")));

            text(ex, ps.executeUpdate() > 0
                    ? "Product Updated Successfully!"
                    : "Product Update Failed!");

        } catch (Exception e) {
            e.printStackTrace();
            text(ex, "Product Update Failed!");
        }
    }

    // =========================================
    // DELETE PRODUCT
    // =========================================

    private static void deleteProduct(HttpExchange ex) throws IOException {
        String data = body(ex);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM products WHERE id=?")) {

            ps.setInt(1, Integer.parseInt(value(data, "id")));

            text(ex, ps.executeUpdate() > 0
                    ? "Product Deleted Successfully!"
                    : "Product Delete Failed!");

        } catch (Exception e) {
            e.printStackTrace();
            text(ex, "Product Delete Failed!");
        }
    }
    // =========================================
// ADD ORDER WITH DELIVERY DETAILS + COD
// =========================================

private static void addOrder(HttpExchange ex) throws IOException {
    String data = body(ex);

    String productName = value(data, "product_name");
    String buyerEmail = value(data, "buyer_email");
    String priceStr = value(data, "price");
    String qtyStr = value(data, "quantity");
    String totalStr = value(data, "total");

    String customerName = value(data, "customer_name");
    String customerPhone = value(data, "customer_phone");
    String customerGender = value(data, "customer_gender");
    String deliveryAddress = value(data, "delivery_address");
    String deliveryCity = value(data, "delivery_city");
    String deliveryPincode = value(data, "delivery_pincode");
    String paymentMethod = value(data, "payment_method");

    if (buyerEmail.isBlank() || productName.isBlank()) {
        send(ex, 400, "Order Placement Failed! Required details missing.",
                "text/plain; charset=UTF-8");
        return;
    }

    if (customerName.isBlank() || customerPhone.isBlank()
            || deliveryAddress.isBlank() || deliveryCity.isBlank()
            || deliveryPincode.isBlank()) {
        send(ex, 400, "Order Placement Failed! Delivery details missing.",
                "text/plain; charset=UTF-8");
        return;
    }

    if (!customerPhone.matches("[0-9]{10}")
            || !deliveryPincode.matches("[0-9]{6}")) {
        send(ex, 400, "Order Placement Failed! Invalid phone or pincode.",
                "text/plain; charset=UTF-8");
        return;
    }

    if (paymentMethod.isBlank()) {
        paymentMethod = "Cash on Delivery";
    }

    if (!"Cash on Delivery".equals(paymentMethod)) {
        send(ex, 400, "Only Cash on Delivery is currently supported.",
                "text/plain; charset=UTF-8");
        return;
    }

    double price;
    double total;
    int quantity;

    try {
        price = Double.parseDouble(priceStr);
        quantity = Integer.parseInt(qtyStr);
        total = totalStr.isBlank()
                ? price * quantity
                : Double.parseDouble(totalStr);

        if (!Double.isFinite(price) || !Double.isFinite(total)
                || price < 0 || total < 0 || quantity < 1) {
            throw new IllegalArgumentException("Invalid order values");
        }
    } catch (Exception e) {
        send(ex, 400, "Order Placement Failed! Invalid price or quantity.",
                "text/plain; charset=UTF-8");
        return;
    }

    String sql = "INSERT INTO orders "
            + "(product_name, price, quantity, total, buyer_email, "
            + "customer_name, customer_phone, customer_gender, "
            + "delivery_address, delivery_city, delivery_pincode, payment_method) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, productName.trim());
        ps.setDouble(2, price);
        ps.setInt(3, quantity);
        ps.setDouble(4, total);
        ps.setString(5, buyerEmail.trim());
        ps.setString(6, customerName.trim());
        ps.setString(7, customerPhone.trim());
        ps.setString(8, customerGender);
        ps.setString(9, deliveryAddress.trim());
        ps.setString(10, deliveryCity.trim());
        ps.setString(11, deliveryPincode.trim());
        ps.setString(12, paymentMethod);

        if (ps.executeUpdate() > 0) {
            text(ex, "Order Placed Successfully!");
        } else {
            send(ex, 500, "Order Placement Failed!",
                    "text/plain; charset=UTF-8");
        }

    } catch (Exception e) {
        e.printStackTrace();
        send(ex, 500, "Order Placement Failed: " + e.getMessage(),
                "text/plain; charset=UTF-8");
    }
}
    
    // =========================================
    // GET ORDERS
    // =========================================

    private static void getOrders(HttpExchange ex) throws IOException {
        String query = ex.getRequestURI().getRawQuery();
        String buyerEmail = query == null ? "" : value(query, "buyer_email");

        StringBuilder json = new StringBuilder("[");

        String sql;
        if (!buyerEmail.isBlank()) {
            sql = "SELECT id, product_name, price, quantity, total, "
                    + "status, order_date, buyer_email FROM orders "
                    + "WHERE buyer_email=? ORDER BY id DESC";
        } else {
            // Admin order list. Do not use this unfiltered request for buyers.
            sql = "SELECT id, product_name, price, quantity, total, "
                    + "status, order_date, buyer_email FROM orders "
                    + "ORDER BY id DESC";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!buyerEmail.isBlank()) {
                ps.setString(1, buyerEmail.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;

                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;

                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    String dateStr = ts != null ? ts.toString() : "";

                    json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"product_name\":").append(jsonString(rs.getString("product_name"))).append(",")
                        .append("\"price\":").append(rs.getDouble("price")).append(",")
                        .append("\"quantity\":").append(rs.getInt("quantity")).append(",")
                        .append("\"total\":").append(rs.getDouble("total")).append(",")
                        .append("\"status\":").append(jsonString(rs.getString("status"))).append(",")
                        .append("\"order_date\":").append(jsonString(dateStr)).append(",")
                        .append("\"buyer_email\":").append(jsonString(rs.getString("buyer_email")))
                        .append("}");
                }
            }

            json.append("]");
            send(ex, 200, json.toString(), "application/json; charset=UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500,
                    "{\"error\":" + jsonString(e.getMessage()) + "}",
                    "application/json; charset=UTF-8");
        }
    }

    // =========================================
    // UPDATE ORDER STATUS
    // =========================================
private static void updateOrderStatus(HttpExchange ex) throws IOException {
    String data = body(ex);
    String status = value(data, "status");
    String idText = value(data, "id");

    if (!status.equals("Placed")
            && !status.equals("Shipped")
            && !status.equals("Delivered")
            && !status.equals("Cancelled")) {
        text(ex, "Invalid Order Status!");
        return;
    }

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(
                 "UPDATE orders SET status=? WHERE id=?")) {

        ps.setString(1, status);
        ps.setInt(2, Integer.parseInt(idText));

        text(ex, ps.executeUpdate() > 0
                ? "Order Status Updated Successfully!"
                : "Order Status Update Failed!");

    } catch (Exception e) {
        e.printStackTrace();
        text(ex, "Order Status Update Failed!");
    }
}

private static void addProductReview(HttpExchange ex)
        throws IOException {

    String data = body(ex);

    String orderIdText = value(data, "order_id");
    String buyerEmail = value(data, "buyer_email");
    String ratingText = value(data, "rating");
    String comment = value(data, "review_comment");

    try {
        int orderId = Integer.parseInt(orderIdText);
        int rating = Integer.parseInt(ratingText);

        if (orderId <= 0 || rating < 1 || rating > 5
                || buyerEmail.isBlank()) {
            send(ex, 400, "Invalid review details",
                    "text/plain; charset=UTF-8");
            return;
        }

        try (Connection con = getConnection()) {
            // Verify that this order belongs to the buyer
            // and has been delivered.
            String productName;

            try (PreparedStatement check = con.prepareStatement(
                    "SELECT product_name FROM orders " +
                    "WHERE id=? AND buyer_email=? AND status='Delivered'")) {

                check.setInt(1, orderId);
                check.setString(2, buyerEmail);

                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        send(ex, 403,
                                "Only the buyer of a delivered order can review it.",
                                "text/plain; charset=UTF-8");
                        return;
                    }

                    productName = rs.getString("product_name");
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO product_reviews " +
                    "(order_id, buyer_email, product_name, rating, review_comment) " +
                    "VALUES (?, ?, ?, ?, ?)")) {

                ps.setInt(1, orderId);
                ps.setString(2, buyerEmail);
                ps.setString(3, productName);
                ps.setInt(4, rating);
                ps.setString(5, comment);

                ps.executeUpdate();

                send(ex, 201, "Review Submitted Successfully!",
                        "text/plain; charset=UTF-8");
            }
        }

    } catch (java.sql.SQLIntegrityConstraintViolationException e) {
        send(ex, 409, "This order has already been reviewed.",
                "text/plain; charset=UTF-8");

    } catch (NumberFormatException e) {
        send(ex, 400, "Invalid order ID or rating.",
                "text/plain; charset=UTF-8");

    } catch (Exception e) {
        e.printStackTrace();
        send(ex, 500, "Unable to submit review.",
                "text/plain; charset=UTF-8");
    }
}

    // =========================================
    // GET USERS
    // =========================================

    private static void getUsers(HttpExchange ex) throws IOException {
        StringBuilder json = new StringBuilder("[");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, name, email, role FROM users ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {

            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"name\":").append(jsonString(rs.getString("name"))).append(",")
                    .append("\"email\":").append(jsonString(rs.getString("email"))).append(",")
                    .append("\"role\":").append(jsonString(rs.getString("role")))
                    .append("}");
            }

            json.append("]");
            send(ex, 200, json.toString(), "application/json; charset=UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500,
                    "{\"error\":" + jsonString(e.getMessage()) + "}",
                    "application/json; charset=UTF-8");
        }
    }

    // =========================================
    // DELETE USER
    // =========================================

    private static void deleteUser(HttpExchange ex) throws IOException {
        String data = body(ex);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM users WHERE id=?")) {

            ps.setInt(1, Integer.parseInt(value(data, "id")));

            text(ex, ps.executeUpdate() > 0
                    ? "User Deleted Successfully!"
                    : "User Delete Failed!");

        } catch (Exception e) {
            e.printStackTrace();
            text(ex, "User Delete Failed!");
        }
    }

    // =========================================
    // HELPERS
    // =========================================

    private static boolean method(HttpExchange ex, String expected)
            throws IOException {
        if (expected.equalsIgnoreCase(ex.getRequestMethod())) {
            return true;
        }

        send(ex, 405, "Method not allowed",
                "text/plain; charset=UTF-8");
        return false;
    }

    private static String body(HttpExchange ex) throws IOException {
        return new String(
                ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static String value(String data, String key) {
        if (data == null || data.isEmpty()) return "";

        for (String part : data.split("&")) {
            String[] pair = part.split("=", 2);

            String k = decode(pair[0]);
            if (k.equals(key)) {
                return pair.length > 1 ? decode(pair[1]) : "";
            }
        }

        return "";
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static String jsonString(String s) {
        if (s == null) return "null";

        StringBuilder out = new StringBuilder("\"");

        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }

        return out.append("\"").toString();
    }

    private static void text(HttpExchange ex, String message)
            throws IOException {
        send(ex, 200, message, "text/plain; charset=UTF-8");
    }

    private static void sendJson(HttpExchange ex, int status, String json)
            throws IOException {
        send(ex, status, json, "application/json; charset=UTF-8");
    }

    private static void send(HttpExchange ex, int status, String content,
                             String contentType) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(status, bytes.length);

        try (var out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static Path resolveFile(String fileName) {
        // 1. Optional override: set WEB_ROOT to the folder holding the HTML/CSS/JS.
        String webRoot = System.getenv("WEB_ROOT");
        if (webRoot != null && !webRoot.isBlank()) {
            Path p = Paths.get(webRoot, fileName);
            if (Files.exists(p)) return p;
        }

        // 2. Folder where the app was started (repo root on Railway).
        Path insideOnlineMart = Paths.get("onlineMart", fileName);
        if (Files.exists(insideOnlineMart)) return insideOnlineMart;

        // 3. Started from inside onlineMart/ itself.
        Path direct = Paths.get(fileName);
        if (Files.exists(direct)) return direct;

        // 4. Started from a sub-folder (for example target/).
        Path inParent = Paths.get("..", "onlineMart", fileName);
        if (Files.exists(inParent)) return inParent;

        return insideOnlineMart;
    }

    private static void file(HttpServer server, String route,
                             String fileName, String contentType) {
        server.createContext(route, ex -> {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                send(ex, 405, "Method not allowed",
                        "text/plain; charset=UTF-8");
                return;
            }

            try {
                Path path = resolveFile(fileName);
                byte[] bytes;

                if (Files.exists(path)) {
                    bytes = Files.readAllBytes(path);
                } else {
                    // Fallback: files bundled inside the jar (used on Railway).
                    try (java.io.InputStream in =
                            SathanaMartBackend.class.getResourceAsStream("/" + fileName)) {
                        if (in == null) {
                            send(ex, 404, "File not found: " + fileName,
                                    "text/plain; charset=UTF-8");
                            return;
                        }
                        bytes = in.readAllBytes();
                    }
                }

                ex.getResponseHeaders().set("Content-Type",
                        contentType + "; charset=UTF-8");
                ex.getResponseHeaders().set("Cache-Control", "no-store");
                ex.sendResponseHeaders(200, bytes.length);

                try (var out = ex.getResponseBody()) {
                    out.write(bytes);
                }

            } catch (Exception e) {
                e.printStackTrace();

                try {
                    send(ex, 500, "Unable to serve " + fileName,
                            "text/plain; charset=UTF-8");
                } catch (IOException ignored) {
                    ex.close();
                }
            }
        });
    }
}
