import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import com.sun.net.httpserver.HttpServer;

public class SathanaMartBackend {


    static final String URL =
            "jdbc:mysql://localhost:3306/SathanaMart";

    static final String USERNAME = "root";

    static final String PASSWORD =
            "sathana2007@";

    public static void main(String[] args) {

        try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            HttpServer server = HttpServer.create(
                    new java.net.InetSocketAddress(8080), 0
            );

            server.createContext("/", exchange -> {

                java.nio.file.Path file =
                        java.nio.file.Paths.get("index.html");

                byte[] content =
                        java.nio.file.Files.readAllBytes(file);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "text/html"
                );

                exchange.sendResponseHeaders(
                        200,
                        content.length
                );

                java.io.OutputStream output =
                        exchange.getResponseBody();

                output.write(content);
                output.close();
            });


            server.createContext("/register", exchange -> {

                if ("POST".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody().readAllBytes()
                            );

                    String[] values =
                            requestData.split("&");

                    String name = "";
                    String email = "";
                    String password = "";
                    String role = "";

                    for (String value : values) {

                        String[] pair =
                                value.split("=", 2);

                        if (pair.length < 2) {
                            continue;
                        }

                        String key = pair[0];

                        String data =
                                java.net.URLDecoder.decode(
                                        pair[1],
                                        "UTF-8"
                                );

                        if (key.equals("name")) {
                            name = data;
                        }

                        if (key.equals("email")) {
                            email = data;
                        }

                        if (key.equals("password")) {
                            password = data;
                        }

                        if (key.equals("role")) {
                            role = data;
                        }
                    }

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

                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain"
                    );

                    exchange.sendResponseHeaders(
                            200,
                            response.length()
                    );

                    java.io.OutputStream output =
                            exchange.getResponseBody();

                    output.write(response.getBytes());
                    output.close();

                } else {

                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }
            });


            server.createContext("/login", exchange -> {

                if ("POST".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody().readAllBytes()
                            );

                    String[] values =
                            requestData.split("&");

                    String email = "";
                    String password = "";
                    String role = "";

                    for (String value : values) {

                        String[] pair =
                                value.split("=", 2);

                        if (pair.length < 2) {
                            continue;
                        }

                        String key = pair[0];

                        String data =
                                java.net.URLDecoder.decode(
                                        pair[1],
                                        "UTF-8"
                                );

                        if (key.equals("email")) {
                            email = data;
                        }

                        if (key.equals("password")) {
                            password = data;
                        }

                        if (key.equals("role")) {
                            role = data;
                        }
                    }

                    String response;

                    try {

                        Connection connection =
                                DriverManager.getConnection(
                                        URL,
                                        USERNAME,
                                        PASSWORD
                                );

                        String sql =
                                "SELECT * FROM users " +
                                "WHERE email = ? " +
                                "AND password = ? " +
                                "AND role = ?";

                        PreparedStatement statement =
                                connection.prepareStatement(sql);

                        statement.setString(1, email);
                        statement.setString(2, password);
                        statement.setString(3, role);

                        ResultSet resultSet =
                                statement.executeQuery();

                        if (resultSet.next()) {

                            response =
                                    "Login Successfully! Welcome to SathanaMart";

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

                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain"
                    );

                    exchange.sendResponseHeaders(
                            200,
                            response.length()
                    );

                    java.io.OutputStream output =
                            exchange.getResponseBody();

                    output.write(response.getBytes());
                    output.close();

                } else {

                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }
            });


            server.createContext("/script.js", exchange -> {

                java.nio.file.Path file =
                        java.nio.file.Paths.get("script.js");

                byte[] content =
                        java.nio.file.Files.readAllBytes(file);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "application/javascript"
                );

                exchange.sendResponseHeaders(
                        200,
                        content.length
                );

                java.io.OutputStream output =
                        exchange.getResponseBody();

                output.write(content);
                output.close();
            });


            server.createContext("/style.css", exchange -> {

                java.nio.file.Path file =
                        java.nio.file.Paths.get("style.css");

                byte[] content =
                        java.nio.file.Files.readAllBytes(file);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "text/css"
                );

                exchange.sendResponseHeaders(
                        200,
                        content.length
                );

                java.io.OutputStream output =
                        exchange.getResponseBody();

                output.write(content);
                output.close();
            });


            server.createContext("/seller.html", exchange -> {

                java.nio.file.Path file =
                        java.nio.file.Paths.get("seller.html");

                byte[] content =
                        java.nio.file.Files.readAllBytes(file);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "text/html"
                );

                exchange.sendResponseHeaders(
                        200,
                        content.length
                );

                java.io.OutputStream output =
                        exchange.getResponseBody();

                output.write(content);
                output.close();
            });


            server.createContext("/seller.js", exchange -> {

                java.nio.file.Path file =
                        java.nio.file.Paths.get("seller.js");

                byte[] content =
                        java.nio.file.Files.readAllBytes(file);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "application/javascript"
                );

                exchange.sendResponseHeaders(
                        200,
                        content.length
                );

                java.io.OutputStream output =
                        exchange.getResponseBody();

                output.write(content);
                output.close();
            });


            server.createContext("/seller.css", exchange -> {

                java.nio.file.Path file =
                        java.nio.file.Paths.get("seller.css");

                byte[] content =
                        java.nio.file.Files.readAllBytes(file);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "text/css"
                );

                exchange.sendResponseHeaders(
                        200,
                        content.length
                );

                java.io.OutputStream output =
                        exchange.getResponseBody();

                output.write(content);
                output.close();
            });


            server.createContext("/products", exchange -> {

                String method =
                        exchange.getRequestMethod();


                if ("POST".equalsIgnoreCase(method)) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody().readAllBytes()
                            );

                    String[] values =
                            requestData.split("&");

                    String name = "";
                    String price = "";
                    String stock = "";
                    String category = "";

                    for (String value : values) {

                        String[] pair =
                                value.split("=", 2);

                        if (pair.length < 2) {
                            continue;
                        }

                        String key = pair[0];

                        String data =
                                java.net.URLDecoder.decode(
                                        pair[1],
                                        "UTF-8"
                                );

                        if (key.equals("name")) {
                            name = data;
                        }

                        if (key.equals("price")) {
                            price = data;
                        }

                        if (key.equals("stock")) {
                            stock = data;
                        }

                        if (key.equals("category")) {
                            category = data;
                        }
                    }

                    String sql =
                            "INSERT INTO products " +
                            "(name, price, stock, category) " +
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
                        statement.setDouble(
                                2,
                                Double.parseDouble(price)
                        );

                        statement.setInt(
                                3,
                                Integer.parseInt(stock)
                        );

                        statement.setString(4, category);

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

                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain"
                    );

                    exchange.sendResponseHeaders(
                            200,
                            response.length()
                    );

                    java.io.OutputStream output =
                            exchange.getResponseBody();

                    output.write(response.getBytes());
                    output.close();


                } else if ("GET".equalsIgnoreCase(method)) {

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
                                "SELECT id, name, price, stock, category " +
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
                                            resultSet.getString("name")
                                    )
                                    .append("\",");

                            json.append("\"price\":")
                                    .append(
                                            resultSet.getDouble("price")
                                    )
                                    .append(",");

                            json.append("\"stock\":")
                                    .append(
                                            resultSet.getInt("stock")
                                    )
                                    .append(",");

                            json.append("\"category\":\"")
                                    .append(
                                            resultSet.getString("category")
                                    )
                                    .append("\"");

                            json.append("}");

                            first = false;
                        }

                        resultSet.close();
                        statement.close();
                        connection.close();

                        json.append("]");

                        String response =
                                json.toString();

                        exchange.getResponseHeaders().set(
                                "Content-Type",
                                "application/json"
                        );

                        exchange.sendResponseHeaders(
                                200,
                                response.length()
                        );

                        java.io.OutputStream output =
                                exchange.getResponseBody();

                        output.write(response.getBytes());
                        output.close();

                    } catch (Exception e) {

                        String response = "[]";

                        exchange.getResponseHeaders().set(
                                "Content-Type",
                                "application/json"
                        );

                        exchange.sendResponseHeaders(
                                500,
                                response.length()
                        );

                        java.io.OutputStream output =
                                exchange.getResponseBody();

                        output.write(response.getBytes());
                        output.close();
                    }


                } else if ("PUT".equalsIgnoreCase(method)) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody().readAllBytes()
                            );

                    String[] values =
                            requestData.split("&");

                    String id = "";
                    String name = "";
                    String price = "";
                    String stock = "";
                    String category = "";

                    for (String value : values) {

                        String[] pair =
                                value.split("=", 2);

                        if (pair.length < 2) {
                            continue;
                        }

                        String key = pair[0];

                        String data =
                                java.net.URLDecoder.decode(
                                        pair[1],
                                        "UTF-8"
                                );

                        if (key.equals("id")) {
                            id = data;
                        }

                        if (key.equals("name")) {
                            name = data;
                        }

                        if (key.equals("price")) {
                            price = data;
                        }

                        if (key.equals("stock")) {
                            stock = data;
                        }

                        if (key.equals("category")) {
                            category = data;
                        }
                    }

                    String sql =
                            "UPDATE products SET " +
                            "name = ?, price = ?, stock = ?, category = ? " +
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

                        statement.setInt(
                                5,
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

                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain"
                    );

                    exchange.sendResponseHeaders(
                            200,
                            response.length()
                    );

                    java.io.OutputStream output =
                            exchange.getResponseBody();

                    output.write(response.getBytes());
                    output.close();


                } else if ("DELETE".equalsIgnoreCase(method)) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody().readAllBytes()
                            );

                    String[] values =
                            requestData.split("&");

                    String id = "";

                    for (String value : values) {

                        String[] pair =
                                value.split("=", 2);

                        if (pair.length < 2) {
                            continue;
                        }

                        String key = pair[0];

                        String data =
                                java.net.URLDecoder.decode(
                                        pair[1],
                                        "UTF-8"
                                );

                        if (key.equals("id")) {
                            id = data;
                        }
                    }

                    String sql =
                            "DELETE FROM products WHERE id = ?";

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

                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain"
                    );

                    exchange.sendResponseHeaders(
                            200,
                            response.length()
                    );

                    java.io.OutputStream output =
                            exchange.getResponseBody();

                    output.write(response.getBytes());
                    output.close();


                } else {

                    exchange.sendResponseHeaders(
                            405,
                            -1
                    );

                    exchange.close();
                }

            });
            server.createContext("/orders", exchange -> {

                String method = exchange.getRequestMethod();

                if ("POST".equalsIgnoreCase(method)) {

                    String requestData =
                            new String(
                                    exchange.getRequestBody().readAllBytes()
                            );

                    String[] values = requestData.split("&");

                    String productName = "";
                    String price = "";
                    String quantity = "";
                    String total = "";

                    for (String value : values) {

                        String[] pair = value.split("=", 2);

                        if (pair.length < 2) {
                            continue;
                        }

                        String key = pair[0];

                        String data =
                                java.net.URLDecoder.decode(
                                        pair[1],
                                        "UTF-8"
                                );

                        if (key.equals("product_name")) {
                            productName = data;
                        }

                        if (key.equals("price")) {
                            price = data;
                        }

                        if (key.equals("quantity")) {
                            quantity = data;
                        }

                        if (key.equals("total")) {
                            total = data;
                        }
                    }

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

                        statement.setString(1, productName);

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
                            response = "Order Placed Successfully!";
                        } else {
                            response = "Order Placement Failed!";
                        }

                        statement.close();
                        connection.close();

                    } catch (Exception e) {
                        response = "Order Placement Failed!";
                    }

                    exchange.getResponseHeaders().set(
                            "Content-Type",
                            "text/plain"
                    );

                    exchange.sendResponseHeaders(
                            200,
                            response.length()
                    );

                    java.io.OutputStream output =
                            exchange.getResponseBody();

                    output.write(response.getBytes());
                    output.close();

                } else if ("GET".equalsIgnoreCase(method)) {

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
                                "SELECT id, product_name, price, quantity, total, status " +
                                "FROM orders ORDER BY id DESC";

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
                                    .append(resultSet.getInt("id"))
                                    .append(",");

                            json.append("\"product_name\":\"")
                                    .append(resultSet.getString("product_name"))
                                    .append("\",");

                            json.append("\"price\":")
                                    .append(resultSet.getDouble("price"))
                                    .append(",");

                            json.append("\"quantity\":")
                                    .append(resultSet.getInt("quantity"))
                                    .append(",");

                            json.append("\"total\":")
                                    .append(resultSet.getDouble("total"))
                                    .append(",");

                            json.append("\"status\":\"")
                                    .append(resultSet.getString("status"))
                                    .append("\"");

                            json.append("}");

                            first = false;
                        }

                        json.append("]");

                        resultSet.close();
                        statement.close();
                        connection.close();

                        String response = json.toString();

                        exchange.getResponseHeaders().set(
                                "Content-Type",
                                "application/json"
                        );

                        exchange.sendResponseHeaders(
                                200,
                                response.length()
                        );

                        java.io.OutputStream output =
                                exchange.getResponseBody();

                        output.write(response.getBytes());
                        output.close();

                    } catch (Exception e) {

                        String response = "[]";

                        exchange.getResponseHeaders().set(
                                "Content-Type",
                                "application/json"
                        );

                        exchange.sendResponseHeaders(
                                500,
                                response.length()
                        );

                        java.io.OutputStream output =
                                exchange.getResponseBody();

                        output.write(response.getBytes());
                        output.close();
                    }

                } else {

                    exchange.sendResponseHeaders(
                            405,
                            -1
                    );

                    exchange.close();
                }

            });


            server.createContext("/buyer.html", exchange -> {
              java.nio.file.Path file = java.nio.file.Paths.get("buyer.html");
              byte[] content = java.nio.file.Files.readAllBytes(file);

              exchange.getResponseHeaders().set("Content-Type", "text/html");
              exchange.sendResponseHeaders(200, content.length);

              java.io.OutputStream output = exchange.getResponseBody();
              output.write(content);
              output.close();
           });

           server.createContext("/buyer.js", exchange -> {
             java.nio.file.Path file = java.nio.file.Paths.get("buyer.js");
             byte[] content = java.nio.file.Files.readAllBytes(file);

             exchange.getResponseHeaders().set("Content-Type", "application/javascript");
             exchange.sendResponseHeaders(200, content.length);

             java.io.OutputStream output = exchange.getResponseBody();
             output.write(content);
             output.close();
           });

           server.createContext("/buyer.css", exchange -> {
             java.nio.file.Path file = java.nio.file.Paths.get("buyer.css");
             byte[] content = java.nio.file.Files.readAllBytes(file);

             exchange.getResponseHeaders().set("Content-Type", "text/css");
             exchange.sendResponseHeaders(200, content.length);

             java.io.OutputStream output = exchange.getResponseBody();
             output.write(content);
             output.close();
          });
 
          server.createContext("/admin.html", exchange -> {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                   serveFile(exchange, "admin.html", "text/html");
                } else {
                   exchange.sendResponseHeaders(405, -1);
                   exchange.close();
                }
          });

           server.createContext("/admin.css", exchange -> {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                   serveFile(exchange, "admin.css", "text/css");
                } else {
                   exchange.sendResponseHeaders(405, -1);
                   exchange.close();
                }
           });

           server.createContext("/admin.js", exchange -> {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    serveFile(exchange, "admin.js", "application/javascript");
                } else {
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }
           });


            server.start();

            System.out.println(
                    "SathanaMart Server Started!"
            );

            System.out.println(
                    "Open: http://localhost:8080"
            );

        } catch (Exception e) {

            System.out.println(
                    "Server Error!"
            );

            System.out.println(
                    e.getMessage()
            );
        }


        Scanner sc =
                new Scanner(System.in);

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
                "\n1. Create Account"
        );

        System.out.println(
                "2. Login"
        );

        System.out.print(
                "\nEnter your choice: "
        );

        int choice =
                sc.nextInt();

        sc.nextLine();


        if (choice == 1) {

            createAccount(sc);

        } else if (choice == 2) {

            login(sc);

        } else {

            System.out.println(
                    "Invalid Choice"
            );
        }

        sc.close();
    }


    static void createAccount(Scanner sc) {

        System.out.println(
                "\n--- CREATE ACCOUNT ---"
        );

        System.out.print(
                "Enter Name: "
        );

        String name =
                sc.nextLine();

        System.out.print(
                "Enter Email: "
        );

        String email =
                sc.nextLine();

        System.out.print(
                "Enter Password: "
        );

        String password =
                sc.nextLine();

        System.out.print(
                "Enter Role (Buyer/Seller): "
        );

        String role =
                sc.nextLine();


        String sql =
                "INSERT INTO users " +
                "(name, email, password, role) " +
                "VALUES (?, ?, ?, ?)";


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

                System.out.println(
                        "\nAccount Created Successfully!"
                );

                System.out.println(
                        "User saved in MySQL database."
                );
            }

            statement.close();
            connection.close();

        } catch (Exception e) {

            System.out.println(
                    "\nAccount Creation Failed!"
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }


    static void login(Scanner sc) {

        System.out.println(
                "\n--- LOGIN ---"
        );

        System.out.print(
                "Enter Email: "
        );

        String email =
                sc.nextLine();

        System.out.print(
                "Enter Password: "
        );

        String password =
                sc.nextLine();

        System.out.print(
                "Enter Role: "
        );

        String role =
                sc.nextLine();


        String sql =
                "SELECT * FROM users " +
                "WHERE email = ? " +
                "AND password = ? " +
                "AND role = ?";


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

            ResultSet result =
                    statement.executeQuery();


            if (result.next()) {

                System.out.println(
                        "\nLogin Successfully!"
                );

                System.out.println(
                        "Welcome to SathanaMart"
                );


                if (role.equalsIgnoreCase(
                        "Seller")) {

                    System.out.println(
                            "\n--- SELLER MODULE ---"
                    );

                    System.out.println(
                            "Seller Login Successful"
                    );

                } else if (
                        role.equalsIgnoreCase(
                                "Buyer")) {

                    System.out.println(
                            "\n--- BUYER MODULE ---"
                    );

                    System.out.println(
                            "Buyer Login Successful"
                    );
                }

            } else {

                System.out.println(
                        "\nInvalid Email, Password or Role"
                );
            }


            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            System.out.println(
                    "\nLogin Failed!"
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }
                static void serveFile(
                     com.sun.net.httpserver.HttpExchange exchange,
                     String fileName,
                     String contentType) throws java.io.IOException {

                     java.nio.file.Path path =
                     java.nio.file.Paths.get(fileName);

                     byte[] data = java.nio.file.Files.readAllBytes(path);

                        exchange.getResponseHeaders().set(
                           "Content-Type",
                             contentType
                        );

                         exchange.sendResponseHeaders(200, data.length);

                         exchange.getResponseBody().write(data);

                         exchange.close();
                }
        
}