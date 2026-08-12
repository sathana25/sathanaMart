import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class sathanaMart {

    static final Scanner sc = new Scanner(System.in);
    static final List<User> users = new ArrayList<>();
    static final List<Product> products = new ArrayList<>();
    static final List<Order> orders = new ArrayList<>();
    static int nextUserId = 1;
    static int nextProductId = 1;
    static int nextOrderId = 1;

    static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    enum Role { BUYER, SELLER, ADMIN }

    static class User {
        int id;
        String name;
        String email;
        String password;
        Role role;

        User(int id, String name, String email, String password, Role role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
        }

        public String toString() {
            return id + " | " + name + " | " + email + " | " + role;
        }
    }

    static class Product {
        int id;
        int sellerId;
        String name;
        String category;
        double price;
        int stock;
        boolean active = true;
        List<Review> reviews = new ArrayList<>();

        Product(int id, int sellerId, String name, String category,
                double price, int stock) {
            this.id = id;
            this.sellerId = sellerId;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        double averageRating() {
            if (reviews.isEmpty()) return 0;
            double sum = 0;
            for (Review r : reviews) sum += r.rating;
            return sum / reviews.size();
        }

        public String toString() {
            return String.format(
                    "ID:%d | %s | Category:%s | Price:Rs.%.2f | Stock:%d | Rating:%.1f",
                    id, name, category, price, stock, averageRating());
        }
    }

    static class CartItem {
        Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double subtotal() {
            return product.price * quantity;
        }
    }

    static class Cart {
        List<CartItem> items = new ArrayList<>();

        void add(Product p, int qty) {
            for (CartItem item : items) {
                if (item.product.id == p.id) {
                    item.quantity += qty;
                    return;
                }
            }
            items.add(new CartItem(p, qty));
        }

        void remove(int productId) {
            items.removeIf(item -> item.product.id == productId);
        }

        void update(int productId, int qty) {
            for (CartItem item : items) {
                if (item.product.id == productId) {
                    if (qty <= 0) remove(productId);
                    else item.quantity = qty;
                    return;
                }
            }
        }

        double total() {
            double total = 0;
            for (CartItem item : items) total += item.subtotal();
            return total;
        }

        void show() {
            if (items.isEmpty()) {
                System.out.println("Cart is empty.");
                return;
            }
            System.out.println("\n--- SHOPPING CART ---");
            for (CartItem item : items) {
                System.out.printf("%d | %s | Qty:%d | Subtotal:Rs.%.2f%n",
                        item.product.id, item.product.name,
                        item.quantity, item.subtotal());
            }
            System.out.printf("TOTAL: Rs.%.2f%n", total());
        }

        void clear() {
            items.clear();
        }
    }

    static class OrderItem {
        String productName;
        double price;
        int quantity;

        OrderItem(String productName, double price, int quantity) {
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        double subtotal() {
            return price * quantity;
        }
    }

    enum OrderStatus { PENDING, DELIVERED }

    static class Order {
        int id;
        int buyerId;
        LocalDateTime date;
        OrderStatus status;
        List<OrderItem> items = new ArrayList<>();

        Order(int id, int buyerId) {
            this.id = id;
            this.buyerId = buyerId;
            this.date = LocalDateTime.now();
            this.status = OrderStatus.PENDING;
        }

        double total() {
            double total = 0;
            for (OrderItem item : items) total += item.subtotal();
            return total;
        }
    }

    static class Review {
        int buyerId;
        int rating;
        String comment;

        Review(int buyerId, int rating, String comment) {
            this.buyerId = buyerId;
            this.rating = rating;
            this.comment = comment;
        }
    }

    public static void main(String[] args) {
        seedAdmin();
        seedProducts();

        while (true) {
            System.out.println("\n==============================");
            System.out.println("        SATHANAMART            ");
            System.out.println("==============================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> {
                    System.out.println("Thank you for using sathanaMart!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------- INITIAL DATA ----------------

    static void seedAdmin() {
        users.add(new User(nextUserId++, "Administrator",
                "admin@shopmart.com", "admin123", Role.ADMIN));
    }

    static void seedProducts() {
        // Optional demo seller and products so buyer screens are not empty.
        User demoSeller = new User(nextUserId++, "Demo Seller",
                "seller@shopmart.com", "seller123", Role.SELLER);
        users.add(demoSeller);

        products.add(new Product(nextProductId++, demoSeller.id,
                "Laptop", "Electronics", 45000, 10));
        products.add(new Product(nextProductId++, demoSeller.id,
                "Mobile", "Electronics", 18000, 15));
        products.add(new Product(nextProductId++, demoSeller.id,
                "Backpack", "Fashion", 1200, 20));
    }

    // ---------------- REGISTRATION / LOGIN ----------------

    static void register() {
        System.out.println("\n--- REGISTER ---");
        String name = readLine("Name: ");
        String email = readLine("Email: ");

        if (findUserByEmail(email) != null) {
            System.out.println("Email already registered.");
            return;
        }

        String password = readLine("Password: ");

        System.out.println("1. Buyer");
        System.out.println("2. Seller");
        int roleChoice = readInt("Select role: ");

        Role role;
        if (roleChoice == 1) role = Role.BUYER;
        else if (roleChoice == 2) role = Role.SELLER;
        else {
            System.out.println("Invalid role.");
            return;
        }

        users.add(new User(nextUserId++, name, email, password, role));
        System.out.println("Registration successful!");
    }

    static void login() {
        System.out.println("\n--- LOGIN ---");
        String email = readLine("Email: ");
        String password = readLine("Password: ");

        User user = findUserByEmail(email);

        if (user == null || !user.password.equals(password)) {
            System.out.println("Invalid email or password.");
            return;
        }

        System.out.println("Login successful. Welcome " + user.name + "!");

        if (user.role == Role.BUYER) buyerMenu(user);
        else if (user.role == Role.SELLER) sellerMenu(user);
        else adminMenu(user);
    }

    // ---------------- SELLER MODULE ----------------

    static void sellerMenu(User seller) {
        while (true) {
            System.out.println("\n--- SELLER MENU ---");
            System.out.println("1. Add Product");
            System.out.println("2. View My Products");
            System.out.println("3. Edit Product");
            System.out.println("4. Delete Product");
            System.out.println("5. View Received Orders");
            System.out.println("6. Mark Order Delivered");
            System.out.println("7. Logout");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> addProduct(seller);
                case 2 -> viewSellerProducts(seller);
                case 3 -> editProduct(seller);
                case 4 -> deleteSellerProduct(seller);
                case 5 -> viewSellerOrders(seller);
                case 6 -> markOrderDelivered(seller);
                case 7 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void addProduct(User seller) {
        System.out.println("\n--- ADD PRODUCT ---");
        String name = readLine("Product name: ");
        String category = readLine("Category: ");
        double price = readDouble("Price: ");
        int stock = readInt("Stock: ");

        if (price < 0 || stock < 0) {
            System.out.println("Price/stock cannot be negative.");
            return;
        }

        products.add(new Product(nextProductId++, seller.id,
                name, category, price, stock));
        System.out.println("Product added successfully.");
    }

    static void viewSellerProducts(User seller) {
        System.out.println("\n--- MY PRODUCTS ---");
        boolean found = false;

        for (Product p : products) {
            if (p.sellerId == seller.id && p.active) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) System.out.println("No products found.");
    }

    static void editProduct(User seller) {
        viewSellerProducts(seller);
        int id = readInt("Enter product ID to edit: ");
        Product p = findProduct(id);

        if (p == null || p.sellerId != seller.id || !p.active) {
            System.out.println("Product not found.");
            return;
        }

        String name = readLine("New name (press Enter to keep): ");
        String category = readLine("New category (press Enter to keep): ");
        String priceText = readLine("New price (press Enter to keep): ");
        String stockText = readLine("New stock (press Enter to keep): ");

        if (!name.isBlank()) p.name = name;
        if (!category.isBlank()) p.category = category;
        if (!priceText.isBlank()) p.price = Double.parseDouble(priceText);
        if (!stockText.isBlank()) p.stock = Integer.parseInt(stockText);

        System.out.println("Product updated successfully.");
    }

    static void deleteSellerProduct(User seller) {
        viewSellerProducts(seller);
        int id = readInt("Enter product ID to delete: ");
        Product p = findProduct(id);

        if (p == null || p.sellerId != seller.id || !p.active) {
            System.out.println("Product not found.");
            return;
        }

        p.active = false;
        System.out.println("Product deleted.");
    }

    static void viewSellerOrders(User seller) {
        System.out.println("\n--- ORDERS RECEIVED ---");
        boolean found = false;

        for (Order o : orders) {
            boolean containsSellerProduct = false;

            for (OrderItem item : o.items) {
                Product p = findProductByName(item.productName);
                if (p != null && p.sellerId == seller.id) {
                    containsSellerProduct = true;
                    break;
                }
            }

            if (containsSellerProduct) {
                printOrder(o);
                found = true;
            }
        }

        if (!found) System.out.println("No orders received.");
    }

    static void markOrderDelivered(User seller) {
        viewSellerOrders(seller);
        int id = readInt("Enter order ID: ");
        Order order = findOrder(id);

        if (order == null || !orderBelongsToSeller(order, seller.id)) {
            System.out.println("Order not found.");
            return;
        }

        order.status = OrderStatus.DELIVERED;
        System.out.println("Order marked as DELIVERED.");
    }

    static boolean orderBelongsToSeller(Order order, int sellerId) {
        for (OrderItem item : order.items) {
            Product p = findProductByName(item.productName);
            if (p != null && p.sellerId == sellerId) return true;
        }
        return false;
    }

    // ---------------- BUYER MODULE ----------------

    static void buyerMenu(User buyer) {
        Cart cart = new Cart();

        while (true) {
            System.out.println("\n--- BUYER MENU ---");
            System.out.println("1. View Products");
            System.out.println("2. Search Products");
            System.out.println("3. Filter by Category");
            System.out.println("4. Add Product to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Remove Product from Cart");
            System.out.println("7. Update Cart Quantity");
            System.out.println("8. Checkout");
            System.out.println("9. Order History");
            System.out.println("10. Give Product Review");
            System.out.println("11. Logout");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> viewProducts();
                case 2 -> searchProducts();
                case 3 -> filterByCategory();
                case 4 -> addToCart(cart);
                case 5 -> cart.show();
                case 6 -> removeFromCart(cart);
                case 7 -> updateCart(cart);
                case 8 -> checkout(buyer, cart);
                case 9 -> orderHistory(buyer);
                case 10 -> addReview(buyer);
                case 11 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void viewProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");
        boolean found = false;

        for (Product p : products) {
            if (p.active && p.stock > 0) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) System.out.println("No products available.");
    }

    static void searchProducts() {
        String keyword = readLine("Search keyword: ").toLowerCase();

        boolean found = false;
        for (Product p : products) {
            if (p.active &&
                    (p.name.toLowerCase().contains(keyword)
                    || p.category.toLowerCase().contains(keyword))) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) System.out.println("No matching products.");
    }

    static void filterByCategory() {
        String category = readLine("Category: ").toLowerCase();

        boolean found = false;
        for (Product p : products) {
            if (p.active && p.category.toLowerCase().equals(category)) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) System.out.println("No products in this category.");
    }

    static void addToCart(Cart cart) {
        viewProducts();
        int id = readInt("Product ID: ");
        Product p = findProduct(id);

        if (p == null || !p.active) {
            System.out.println("Product not found.");
            return;
        }

        int qty = readInt("Quantity: ");

        if (qty <= 0 || qty > p.stock) {
            System.out.println("Invalid quantity. Available stock: " + p.stock);
            return;
        }

        // Check quantity already in cart.
        int existing = 0;
        for (CartItem item : cart.items) {
            if (item.product.id == p.id) existing = item.quantity;
        }

        if (existing + qty > p.stock) {
            System.out.println("Total quantity exceeds available stock.");
            return;
        }

        cart.add(p, qty);
        System.out.println("Product added to cart.");
    }

    static void removeFromCart(Cart cart) {
        cart.show();
        int id = readInt("Product ID to remove: ");
        cart.remove(id);
        System.out.println("Product removed if it existed.");
    }

    static void updateCart(Cart cart) {
        cart.show();
        int id = readInt("Product ID: ");
        int qty = readInt("New quantity: ");

        Product p = findProduct(id);
        if (p == null || !p.active || qty > p.stock) {
            System.out.println("Invalid product or quantity.");
            return;
        }

        cart.update(id, qty);
        System.out.println("Cart updated.");
    }

    // ---------------- CHECKOUT / ORDERS ----------------

    static void checkout(User buyer, Cart cart) {
        if (cart.items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        cart.show();
        System.out.println("No real payment is required.");
        String confirm = readLine("Confirm order? (yes/no): ");

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Order cancelled.");
            return;
        }

        // Recheck stock before placing order.
        for (CartItem item : cart.items) {
            if (!item.product.active || item.quantity > item.product.stock) {
                System.out.println("Checkout failed: insufficient stock for "
                        + item.product.name);
                return;
            }
        }

        Order order = new Order(nextOrderId++, buyer.id);

        for (CartItem item : cart.items) {
            order.items.add(new OrderItem(
                    item.product.name,
                    item.product.price,
                    item.quantity
            ));
            item.product.stock -= item.quantity;
        }

        orders.add(order);
        cart.clear();

        System.out.println("\nOrder Successful!");
        System.out.println("Order ID: " + order.id);
        System.out.printf("Order Total: Rs.%.2f%n", order.total());
    }

    static void orderHistory(User buyer) {
        System.out.println("\n--- ORDER HISTORY ---");
        boolean found = false;

        for (Order o : orders) {
            if (o.buyerId == buyer.id) {
                printOrder(o);
                found = true;
            }
        }

        if (!found) System.out.println("No orders found.");
    }

    static void printOrder(Order o) {
        System.out.println("\nOrder ID: " + o.id);
        System.out.println("Date: " + o.date.format(TIME));
        System.out.println("Status: " + o.status);

        for (OrderItem item : o.items) {
            System.out.printf("  %s | Qty:%d | Rs.%.2f%n",
                    item.productName, item.quantity, item.subtotal());
        }

        System.out.printf("Total: Rs.%.2f%n", o.total());
    }

    // ---------------- REVIEWS ----------------

    static void addReview(User buyer) {
        viewProducts();
        int productId = readInt("Product ID to review: ");
        Product p = findProduct(productId);

        if (p == null || !p.active) {
            System.out.println("Product not found.");
            return;
        }

        if (!buyerPurchasedProduct(buyer.id, productId, p.name)) {
            System.out.println("You can review a product only after purchasing it.");
            return;
        }

        int rating = readInt("Rating (1-5): ");
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }

        String comment = readLine("Comment: ");
        p.reviews.add(new Review(buyer.id, rating, comment));

        System.out.println("Review added successfully.");
    }

    static boolean buyerPurchasedProduct(int buyerId, int productId,
                                         String productName) {
        for (Order o : orders) {
            if (o.buyerId != buyerId) continue;

            for (OrderItem item : o.items) {
                if (item.productName.equals(productName)) return true;
            }
        }
        return false;
    }

    // ---------------- ADMIN MODULE ----------------

    static void adminMenu(User admin) {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View All Users");
            System.out.println("2. View All Products");
            System.out.println("3. View All Orders");
            System.out.println("4. Remove Inappropriate Product");
            System.out.println("5. Logout");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewProducts();
                case 3 -> viewAllOrders();
                case 4 -> adminDeleteProduct();
                case 5 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void viewAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        for (User u : users) System.out.println(u);
    }

    static void viewAllOrders() {
        System.out.println("\n--- ALL ORDERS ---");

        if (orders.isEmpty()) {
            System.out.println("No orders.");
            return;
        }

        for (Order o : orders) printOrder(o);
    }

    static void adminDeleteProduct() {
        viewProducts();
        int id = readInt("Product ID to remove: ");
        Product p = findProduct(id);

        if (p == null || !p.active) {
            System.out.println("Product not found.");
            return;
        }

        p.active = false;
        System.out.println("Product removed by admin.");
    }

    // ---------------- SEARCH HELPERS ----------------

    static User findUserByEmail(String email) {
        for (User u : users) {
            if (u.email.equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    static Product findProduct(int id) {
        for (Product p : products) {
            if (p.id == id) return p;
        }
        return null;
    }

    /*
     * Product names are stored inside old orders so that order history remains
     * readable even if a product is deleted. This helper is used only for
     * matching seller ownership of currently existing products.
     */
    static Product findProductByName(String name) {
        for (Product p : products) {
            if (p.name.equals(name)) return p;
        }
        return null;
    }

    static Order findOrder(int id) {
        for (Order o : orders) {
            if (o.id == id) return o;
        }
        return null;
    }

    // ---------------- INPUT HELPERS ----------------

    static String readLine(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    static int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static double readDouble(String message) {
        while (true) {
            try {
                return Double.parseDouble(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid price.");
            }
        }
    }
}
