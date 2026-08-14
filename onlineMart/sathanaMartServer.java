import java.util.Scanner;

public class SathanaMartBackend {

    static Scanner sc = new Scanner(System.in);

    static String name;
    static String email;
    static String password;
    static String role;

    static boolean accountCreated = false;

    public static void main(String[] args) {

        System.out.println("===== SATHANAMART BACKEND =====");

        createAccount();

        login();
    }


    static void createAccount() {

        System.out.println("\n--- CREATE ACCOUNT ---");

        System.out.print("Enter Name: ");
        name = sc.nextLine();

        System.out.print("Enter Email: ");
        email = sc.nextLine();

        System.out.print("Create Password: ");
        password = sc.nextLine();

        System.out.print("Enter Role (Buyer/Seller): ");
        role = sc.nextLine();

        accountCreated = true;

        System.out.println("\nAccount Created Successfully!");
    }


    static void login() {

        System.out.println("\n--- LOGIN ---");

        System.out.print("Enter Email: ");
        String loginEmail = sc.nextLine();

        if (!loginEmail.equals(email)) {
            System.out.println("Invalid Email ID");
            return;
        }

        System.out.print("Enter Password: ");
        String loginPassword = sc.nextLine();

        if (!loginPassword.equals(password)) {
            System.out.println("Invalid Password");
            return;
        }

        System.out.println("\nLogin Successfully!");
        System.out.println("Welcome to SathanaMart");
        System.out.println("Role: " + role);


        if (role.equalsIgnoreCase("Seller")) {

            System.out.println("\n--- SELLER MODULE ---");
            System.out.println("1. Laptop");
            System.out.println("2. Earphones");
            System.out.println("3. Mobile");
            System.out.println("4. Smart Watch");
            System.out.println("5. Camera");

        } else {

            System.out.println("\n--- BUYER MODULE ---");
            System.out.println("Welcome Buyer!");
        }
    }
}