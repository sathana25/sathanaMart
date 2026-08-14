import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SathanaMartBackend {

    // Temporary account storage
    static String name = "";
    static String email = "";
    static String password = "";
    static String role = "";

    static boolean accountCreated = false;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext(
                "/register",
                SathanaMartBackend::register
        );

        server.createContext(
                "/login",
                SathanaMartBackend::login
        );

        server.createContext(
                "/",
                SathanaMartBackend::home
        );

        server.start();

        System.out.println("--------------------------------");
        System.out.println("SathanaMart Java Backend Started");
        System.out.println("--------------------------------");
        System.out.println("Server: http://localhost:8080");
    }


    // HOME
    static void home(HttpExchange exchange)
            throws IOException {

        sendResponse(
                exchange,
                "SathanaMart Java Backend is Running!"
        );
    }


    // CREATE ACCOUNT
    static void register(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Only POST request allowed."
            );

            return;
        }

        String data =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> form =
                parseData(data);

        name = form.get("name");
        email = form.get("email");
        password = form.get("password");
        role = form.get("role");

        accountCreated = true;

        sendResponse(
                exchange,
                "Account Created Successfully!"
        );
    }


    // LOGIN
    static void login(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Only POST request allowed."
            );

            return;
        }

        if (!accountCreated) {

            sendResponse(
                    exchange,
                    "Please create an account first."
            );

            return;
        }

        String data =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> form =
                parseData(data);

        String loginEmail =
                form.get("email");

        String loginPassword =
                form.get("password");

        String loginRole =
                form.get("role");


        // EMAIL CHECK
        if (!loginEmail.equals(email)) {

            sendResponse(
                    exchange,
                    "Invalid Email ID"
            );

            return;
        }


        // PASSWORD CHECK
        if (!loginPassword.equals(password)) {

            sendResponse(
                    exchange,
                    "Invalid Password"
            );

            return;
        }


        // ROLE CHECK
        if (!loginRole.equalsIgnoreCase(role)) {

            sendResponse(
                    exchange,
                    "Invalid Role"
            );

            return;
        }


        // SUCCESS
        sendResponse(
                exchange,
                "Login Successfully! Welcome to SathanaMart"
        );
    }


    // CONVERT FORM DATA
    static Map<String, String> parseData(
            String data) {

        Map<String, String> result =
                new HashMap<>();

        String[] pairs = data.split("&");

        for (String pair : pairs) {

            String[] parts =
                    pair.split("=", 2);

            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8
                        );

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );

                result.put(key, value);
            }
        }

        return result;
    }


    // SEND RESPONSE
    static void sendResponse(
            HttpExchange exchange,
            String response)
            throws IOException {

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(bytes);
        output.close();
    }
}