import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class SathanaMartBackend {
        private static final String DB_HOST =
        System.getenv().getOrDefault("SATHANAMART_DB_HOST", "localhost");

private static final String DB_PORT =
        System.getenv().getOrDefault("SATHANAMART_DB_PORT", "3306");

private static final String DB_NAME =
        System.getenv().getOrDefault("SATHANAMART_DB_NAME", "SathanaMart");

private static final String USERNAME =
        System.getenv().getOrDefault("SATHANAMART_DB_USER", "root");

private static final String PASSWORD =
        System.getenv("SATHANAMART_DB_PASSWORD");


private static final String URL =
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?sslMode=REQUIRED&allowPublicKeyRetrieval=true&serverTimezone=UTC";        



    public static void main(String[] args) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");


                    int port = Integer.parseInt(
    System.getenv().getOrDefault("PORT", "8080")
);

HttpServer server = HttpServer.create(
    new InetSocketAddress("0.0.0.0", port),
    0
);


            // =========================================
            // HOME PAGE
            // =========================================

            server.createContext("/", exchange -> {

                serveFile(
                        exchange,
                        "index.html",
                        "text/html"
                );

            });


            // =========================================
            // REGISTER
            // =========================================

            server.createContext("/register", exchange -> {

                if (!"POST".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                    return;
                }


                String requestData =
                        new String(
                                exchange.getRequestBody()
                                        .readAllBytes()
                        );


                String name =
                        getValue(requestData, "name");

                String email =
                        getValue(requestData, "email");

                String password =
                        getValue(requestData, "password");

                String role =
                        getValue(requestData, "role");


                String sql =
                        "INSERT INTO users " +
                        "(name, email, password, role) " +
                        "VALUES (?, ?, ?, ?)";


                String response;


                try {

                    Connection connection =
                            DriverManager.getConnection(
                                    URL,
                                    USERNAME,
                                    PASSWORD
                            );


                    PreparedStatement statement =
                            connection.prepareStatement(sql);


                    statement.setString(1, name);
                    statement.setString(2, email);
                    statement.setString(3, password);
                    statement.setString(4, role);


                    int result =
                            statement.executeUpdate();


                    if (result > 0) {

                        response =
                                "Account Created Successfully!";

                    } else {

                        response =
                                "Account Creation Failed!";
                    }


                    statement.close();
                    connection.close();

                } catch (Exception e) {

                    response =
                            "Account Creation Failed!";
                }


                sendText(
                        exchange,
                        response
                );

            });


            // =========================================
            // LOGIN
            // =========================================

            server.createContext("/login", exchange -> {

                if (!"POST".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                    return;
                }


                String requestData =
                        new String(
                                exchange.getRequestBody()
                                        .readAllBytes()
                        );


                String email =
                        getValue(requestData, "email");

                String password =
                        getValue(requestData, "password");

                String role =
                        getValue(requestData, "role");


                String sql =
                        "SELECT * FROM users " +
                        "WHERE email = ? " +
                        "AND password = ? " +
                        "AND role = ?";


                String response;


                try {

                    Connection connection =
                            DriverManager.getConnection(
                                    URL,
                                    USERNAME,
                                    PASSWORD
                            );


                    PreparedStatement statement =
                            connection.prepareStatement(sql);


                    statement.setString(1, email);
                    statement.setString(2, password);
                    statement.setString(3, role);


                    ResultSet resultSet =
                            statement.executeQuery();


                    if (resultSet.next()) {

                        response =
                                "Login Successfully! " +
                                "Welcome to SathanaMart";

                    } else {

                        response =
                                "Invalid Email, Password or Role!";
                    }


                    resultSet.close();
                    statement.close();
                    connection.close();

                } catch (Exception e) {

                    response =
                            "Login Failed!";
                }


                sendText(
                        exchange,
                        response
                );

            });


            // =========================================
            // FRONTEND FILES
            // =========================================

            createFileRoute(
                    server,
                    "/script.js",
                    "script.js",
                    "application/javascript"
            );


            createFileRoute(
                    server,
                    "/style.css",
                    "style.css",
                    "text/css"
            );


            createFileRoute(
                    server,
                    "/seller.html",
                    "seller.html",
                    "text/html"
            );


            createFileRoute(
                    server,
                    "/seller.js",
                    "seller.js",
                    "application/javascript"
            );


            createFileRoute(
                    server,
                    "/seller.css",
                    "seller.css",
                    "text/css"
            );


            createFileRoute(
                    server,
                    "/buyer.html",
                    "buyer.html",
                    "text/html"
            );


            createFileRoute(
                    server,
                    "/buyer.js",
                    "buyer.js",
                    "application/javascript"
            );


            createFileRoute(
                    server,
                    "/buyer.css",
                    "buyer.css",
                    "text/css"
            );


            createFileRoute(
                    server,
                    "/admin.html",
                    "admin.html",
                    "text/html"
            );


            createFileRoute(
                    server,
                    "/admin.js",
                    "admin.js",
                    "application/javascript"
            );


            createFileRoute(
                    server,
                    "/admin.css",
                    "admin.css",
                    "text/css"
            );


            // =========================================
            // CART PAGE
            // =========================================

            createFileRoute(
                    server,
                    "/cart.html",
                    "cart.html",
                    "text/html"
            );


            createFileRoute(
                    server,
                    "/cart.js",
                    "cart.js",
                    "application/javascript"
            );


            createFileRoute(
                    server,
                    "/cart.css",
                    "cart.css",
                    "text/css"
            );


            // =========================================
            // WISHLIST PAGE
            // =========================================

            createFileRoute(
                    server,
                    "/wishlist.html",
                    "wishlist.html",
                    "text/html"
            );


            createFileRoute(
                    server,
                    "/wishlist.js",
                    "wishlist.js",
                    "application/javascript"
            );


            createFileRoute(
                    server,
                    "/wishlist.css",
                    "wishlist.css",
                    "text/css"
            );


            // =========================================
            // PRODUCTS
            // =========================================

            server.createContext(
                    "/products",
                    exchange -> {

                String method =
                        exchange.getRequestMethod();


                // -------------------------------------
                // ADD PRODUCT
                // -------------------------------------

                if ("POST".equalsIgnoreCase(method)) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes()
                            );


                    String name =
                            getValue(
                                    requestData,
                                    "name"
                            );

                    String price =
                            getValue(
                                    requestData,
                                    "price"
                            );

                    String stock =
                            getValue(
                                    requestData,
                                    "stock"
                            );

                    String category =
                            getValue(
                                    requestData,
                                    "category"
                            );

                    String image =
                            getValue(
                                    requestData,
                                    "image"
                            );


                    String sql =
                            "INSERT INTO products " +
                            "(name, price, stock, category, image) " +
                            "VALUES (?, ?, ?, ?, ?)";


                    String response;


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        statement.setString(1, name);

                        statement.setDouble(
                                2,
                                Double.parseDouble(price)
                        );

                        statement.setInt(
                                3,
                                Integer.parseInt(stock)
                        );

                        statement.setString(
                                4,
                                category
                        );

                        statement.setString(
                                5,
                                image
                        );


                        int result =
                                statement.executeUpdate();


                        if (result > 0) {

                            response =
                                    "Product Added Successfully!";

                        } else {

                            response =
                                    "Product Addition Failed!";
                        }


                        statement.close();
                        connection.close();

                    } catch (Exception e) {

                        response =
                                "Product Addition Failed!";
                    }


                    sendText(
                            exchange,
                            response
                    );


                // -------------------------------------
                // GET PRODUCTS
                // -------------------------------------

                } else if (
                        "GET".equalsIgnoreCase(method)
                ) {

                    StringBuilder json =
                            new StringBuilder();


                    json.append("[");


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        String sql =
                                "SELECT id, name, price, " +
                                "stock, category, image " +
                                "FROM products";


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        ResultSet resultSet =
                                statement.executeQuery();


                        boolean first = true;


                        while (resultSet.next()) {

                            if (!first) {

                                json.append(",");

                            }


                            json.append("{");


                            json.append("\"id\":")
                                    .append(
                                            resultSet.getInt("id")
                                    )
                                    .append(",");


                            json.append("\"name\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                    "name"
                                                )
                                            )
                                    )
                                    .append("\",");


                            json.append("\"price\":")
                                    .append(
                                            resultSet.getDouble(
                                                    "price"
                                            )
                                    )
                                    .append(",");


                            json.append("\"stock\":")
                                    .append(
                                            resultSet.getInt(
                                                    "stock"
                                            )
                                    )
                                    .append(",");


                            json.append("\"category\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                    "category"
                                                )
                                            )
                                    )
                                    .append("\",");


                            String image =
                                    resultSet.getString(
                                            "image"
                                    );


                            json.append("\"image\":\"")
                                    .append(
                                            escapeJson(
                                                image == null
                                                        ? ""
                                                        : image
                                            )
                                    )
                                    .append("\"");


                            json.append("}");


                            first = false;
                        }


                        json.append("]");


                        resultSet.close();
                        statement.close();
                        connection.close();


                        sendJson(
                                exchange,
                                200,
                                json.toString()
                        );


                    } catch (Exception e) {

                        sendJson(
                                exchange,
                                500,
                                "[]"
                        );
                    }


                // -------------------------------------
                // UPDATE PRODUCT
                // -------------------------------------

                } else if (
                        "PUT".equalsIgnoreCase(method)
                ) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes()
                            );


                    String id =
                            getValue(
                                    requestData,
                                    "id"
                            );

                    String name =
                            getValue(
                                    requestData,
                                    "name"
                            );

                    String price =
                            getValue(
                                    requestData,
                                    "price"
                            );

                    String stock =
                            getValue(
                                    requestData,
                                    "stock"
                            );

                    String category =
                            getValue(
                                    requestData,
                                    "category"
                            );

                    String image =
                            getValue(
                                    requestData,
                                    "image"
                            );


                    String sql =
                            "UPDATE products SET " +
                            "name = ?, " +
                            "price = ?, " +
                            "stock = ?, " +
                            "category = ?, " +
                            "image = ? " +
                            "WHERE id = ?";


                    String response;


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        statement.setString(
                                1,
                                name
                        );


                        statement.setDouble(
                                2,
                                Double.parseDouble(price)
                        );


                        statement.setInt(
                                3,
                                Integer.parseInt(stock)
                        );


                        statement.setString(
                                4,
                                category
                        );


                        statement.setString(
                                5,
                                image
                        );


                        statement.setInt(
                                6,
                                Integer.parseInt(id)
                        );


                        int result =
                                statement.executeUpdate();


                        if (result > 0) {

                            response =
                                    "Product Updated Successfully!";

                        } else {

                            response =
                                    "Product Update Failed!";
                        }


                        statement.close();
                        connection.close();

                    } catch (Exception e) {

                        response =
                                "Product Update Failed!";
                    }


                    sendText(
                            exchange,
                            response
                    );


                // -------------------------------------
                // DELETE PRODUCT
                // -------------------------------------

                } else if (
                        "DELETE".equalsIgnoreCase(method)
                ) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes()
                            );


                    String id =
                            getValue(
                                    requestData,
                                    "id"
                            );


                    String response;


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        String sql =
                                "DELETE FROM products " +
                                "WHERE id = ?";


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        statement.setInt(
                                1,
                                Integer.parseInt(id)
                        );


                        int result =
                                statement.executeUpdate();


                        if (result > 0) {

                            response =
                                    "Product Deleted Successfully!";

                        } else {

                            response =
                                    "Product Delete Failed!";
                        }


                        statement.close();
                        connection.close();

                    } catch (Exception e) {

                        response =
                                "Product Delete Failed!";
                    }


                    sendText(
                            exchange,
                            response
                    );


                } else {

                    exchange.sendResponseHeaders(
                            405,
                            -1
                    );

                    exchange.close();
                }

            });


            // =========================================
            // ORDERS
            // =========================================

            server.createContext(
                    "/orders",
                    exchange -> {

                String method =
                        exchange.getRequestMethod();


                // -------------------------------------
                // CREATE ORDER
                // -------------------------------------

                if ("POST".equalsIgnoreCase(method)) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes()
                            );


                    String productName =
                            getValue(
                                    requestData,
                                    "product_name"
                            );

                    String price =
                            getValue(
                                    requestData,
                                    "price"
                            );

                    String quantity =
                            getValue(
                                    requestData,
                                    "quantity"
                            );

                    String total =
                            getValue(
                                    requestData,
                                    "total"
                            );


                    String sql =
                            "INSERT INTO orders " +
                            "(product_name, price, quantity, total) " +
                            "VALUES (?, ?, ?, ?)";


                    String response;


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        statement.setString(
                                1,
                                productName
                        );


                        statement.setDouble(
                                2,
                                Double.parseDouble(price)
                        );


                        statement.setInt(
                                3,
                                Integer.parseInt(quantity)
                        );


                        statement.setDouble(
                                4,
                                Double.parseDouble(total)
                        );


                        int result =
                                statement.executeUpdate();


                        if (result > 0) {

                            response =
                                    "Order Placed Successfully!";

                        } else {

                            response =
                                    "Order Placement Failed!";
                        }


                        statement.close();
                        connection.close();

                    } catch (Exception e) {

                        response =
                                "Order Placement Failed!";
                    }


                    sendText(
                            exchange,
                            response
                    );


                // -------------------------------------
                // UPDATE ORDER STATUS
                // -------------------------------------

                } else if (
                        "PUT".equalsIgnoreCase(method)
                ) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes()
                            );


                    String id =
                            getValue(
                                    requestData,
                                    "id"
                            );

                    String status =
                            getValue(
                                    requestData,
                                    "status"
                            );


                    String response;


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        String sql =
                                "UPDATE orders " +
                                "SET status = ? " +
                                "WHERE id = ?";


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        statement.setString(
                                1,
                                status
                        );


                        statement.setInt(
                                2,
                                Integer.parseInt(id)
                        );


                        int result =
                                statement.executeUpdate();


                        if (result > 0) {

                            response =
                                    "Order Status Updated Successfully!";

                        } else {

                            response =
                                    "Order Status Update Failed!";
                        }


                        statement.close();
                        connection.close();

                    } catch (Exception e) {

                        response =
                                "Order Status Update Failed!";
                    }


                    sendText(
                            exchange,
                            response
                    );


                // -------------------------------------
                // GET ORDERS
                // -------------------------------------

                } else if (
                        "GET".equalsIgnoreCase(method)
                ) {

                    StringBuilder json =
                            new StringBuilder();


                    json.append("[");


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        String sql =
                                "SELECT id, product_name, " +
                                "price, quantity, total, " +
                                "status, order_date " +
                                "FROM orders " +
                                "ORDER BY id DESC";


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        ResultSet resultSet =
                                statement.executeQuery();


                        boolean first = true;


                        while (resultSet.next()) {

                            if (!first) {

                                json.append(",");

                            }


                            json.append("{");


                            json.append("\"id\":")
                                    .append(
                                            resultSet.getInt("id")
                                    )
                                    .append(",");


                            json.append(
                                    "\"product_name\":\""
                            )
                            .append(
                                    escapeJson(
                                        resultSet.getString(
                                                "product_name"
                                        )
                                    )
                            )
                            .append("\",");


                            json.append("\"price\":")
                                    .append(
                                            resultSet.getDouble(
                                                    "price"
                                            )
                                    )
                                    .append(",");


                            json.append("\"quantity\":")
                                    .append(
                                            resultSet.getInt(
                                                    "quantity"
                                            )
                                    )
                                    .append(",");


                            json.append("\"total\":")
                                    .append(
                                            resultSet.getDouble(
                                                    "total"
                                            )
                                    )
                                    .append(",");


                            json.append("\"status\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                        "status"
                                                )
                                            )
                                    )
                                    .append("\",");


                            json.append("\"order_date\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                        "order_date"
                                                )
                                            )
                                    )
                                    .append("\"");


                            json.append("}");


                            first = false;
                        }


                        json.append("]");


                        resultSet.close();
                        statement.close();
                        connection.close();


                        sendJson(
                                exchange,
                                200,
                                json.toString()
                        );


                    } catch (Exception e) {

                        sendJson(
                                exchange,
                                500,
                                "[]"
                        );
                    }


                } else {

                    exchange.sendResponseHeaders(
                            405,
                            -1
                    );

                    exchange.close();
                }

            });


            // =========================================
            // USERS
            // =========================================

            server.createContext(
                    "/users",
                    exchange -> {

                String method =
                        exchange.getRequestMethod();


                // GET USERS

                if ("GET".equalsIgnoreCase(method)) {

                    StringBuilder json =
                            new StringBuilder();


                    json.append("[");


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        String sql =
                                "SELECT id, name, email, role " +
                                "FROM users " +
                                "ORDER BY id DESC";


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        ResultSet resultSet =
                                statement.executeQuery();


                        boolean first = true;


                        while (resultSet.next()) {

                            if (!first) {

                                json.append(",");

                            }


                            json.append("{");


                            json.append("\"id\":")
                                    .append(
                                            resultSet.getInt("id")
                                    )
                                    .append(",");


                            json.append("\"name\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                        "name"
                                                )
                                            )
                                    )
                                    .append("\",");


                            json.append("\"email\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                        "email"
                                                )
                                            )
                                    )
                                    .append("\",");


                            json.append("\"role\":\"")
                                    .append(
                                            escapeJson(
                                                resultSet.getString(
                                                        "role"
                                                )
                                            )       
                                    )
                                    .append("\"");


                            json.append("}");


                            first = false;
                        }


                        json.append("]");


                        resultSet.close();
                        statement.close();
                        connection.close();


                        sendJson(
                                exchange,
                                200,
                                json.toString()
                        );


                    } catch (Exception e) {

                        sendJson(
                                exchange,
                                500,
                                "[]"
                        );
                    }


                // DELETE USER

                } else if (
                        "DELETE".equalsIgnoreCase(method)
                ) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes()
                            );


                    String id =
                            getValue(
                                    requestData,
                                    "id"
                            );


                    String response;


                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );


                        String sql =
                                "DELETE FROM users " +
                                "WHERE id = ?";


                        PreparedStatement statement =
                                connection.prepareStatement(sql);


                        statement.setInt(
                                1,
                                Integer.parseInt(id)
                        );


                        int result =
                                statement.executeUpdate();


                        if (result > 0) {

                            response =
                                    "User Deleted Successfully!";

                        } else {

                            response =
                                    "User Delete Failed!";
                        }


                        statement.close();
                        connection.close();

                    } catch (Exception e) {

                        response =
                                "User Delete Failed!";
                    }


                    sendText(
                            exchange,
                            response
                    );


                } else {

                    exchange.sendResponseHeaders(
                            405,
                            -1
                    );

                    exchange.close();
                }

            });


            // =========================================
            // START SERVER
            // =========================================

            server.start();


            System.out.println();
            System.out.println(
                    "SathanaMart Server Started!"
            );

            System.out.println(
                    "Open: http://localhost:8080"
            );

            System.out.println();
            System.out.println(
                    "================================"
            );

            System.out.println(
                    "      SATHANAMART BACKEND"
            );

            System.out.println(
                    "================================"
            );


            System.out.println(
                    "Server is running..."
            );


            // Keep server running

            Scanner sc =
                    new Scanner(System.in);

            System.out.println(
                    "Press ENTER to stop the server."
            );

            sc.nextLine();

            sc.close();


        } catch (Exception e) {

            System.out.println(
                    "Server Error!"
            );

            System.out.println(
                    e.getMessage()
            );
        }

    }


    // =========================================
    // HELPER - GET FORM VALUE
    // =========================================

    static String getValue(
            String requestData,
            String key) {

        String[] values =
                requestData.split("&");


        for (String value : values) {

            String[] pair =
                    value.split("=", 2);


            if (pair.length < 2) {

                continue;

            }


            if (pair[0].equals(key)) {

                return java.net.URLDecoder.decode(
                        pair[1],
                        java.nio.charset.StandardCharsets.UTF_8
                );

            }

        }


        return "";

    }


    // =========================================
    // HELPER - SERVE FILE
    // =========================================

    static void serveFile(
            HttpExchange exchange,
            String fileName,
            String contentType)
            throws java.io.IOException {


        java.nio.file.Path path =
                java.nio.file.Paths.get(
                        fileName
                );


        byte[] data =
                java.nio.file.Files.readAllBytes(
                        path
                );


        exchange.getResponseHeaders().set(
                "Content-Type",
                contentType
        );


        exchange.sendResponseHeaders(
                200,
                data.length
        );


        exchange.getResponseBody().write(
                data
        );


        exchange.close();

    }


    // =========================================
    // HELPER - CREATE FILE ROUTE
    // =========================================

    static void createFileRoute(
            HttpServer server,
            String route,
            String fileName,
            String contentType) {


        server.createContext(
                route,
                exchange -> {

                    if (
                        "GET".equalsIgnoreCase(
                            exchange.getRequestMethod()
                        )
                    ) {

                        serveFile(
                                exchange,
                                fileName,
                                contentType
                        );

                    } else {

                        exchange.sendResponseHeaders(
                                405,
                                -1
                        );

                        exchange.close();
                    }

                }
        );

    }


    // =========================================
    // HELPER - SEND TEXT
    // =========================================

    static void sendText(
            HttpExchange exchange,
            String response)
            throws java.io.IOException {


        byte[] data =
                response.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );


        exchange.sendResponseHeaders(
                200,
                data.length
        );


        exchange.getResponseBody().write(
                data
        );


        exchange.close();

    }


    // =========================================
    // HELPER - SEND JSON
    // =========================================

    static void sendJson(
            HttpExchange exchange,
            int status,
            String response)
            throws java.io.IOException {


        byte[] data =
                response.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );


        exchange.sendResponseHeaders(
                status,
                data.length
        );


        exchange.getResponseBody().write(
                data
        );


        exchange.close();

    }


    // =========================================
    // HELPER - JSON ESCAPE
    // =========================================

    static String escapeJson(
            String value) {

        if (value == null) {

            return "";

        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );

    }

}