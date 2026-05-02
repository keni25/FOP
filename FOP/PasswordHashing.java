package fop_pasti_assignment;

import BCrypt.BCrypt;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class PasswordHashing {
    //Register a new user
    public static void register(Scanner sc, String[] names, String[] emails, String[] passwords, int[] userCount) throws IOException {
        System.out.println("\n== Please fill in the form ==");
        sc.nextLine();

        String name = "";
        String email = "";
        String password = "";

        // Get valid name input
        while (true) {
            System.out.print("Name: ");
            name = sc.nextLine();
            if (isValidName(name)) {
                break;
            } else {
                System.out.println("Invalid name.");
            }
        }

        // Get valid email input
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine();
            if (isValidEmail(email)) {
                break;
            } else {
                System.out.println("Invalid email. The correct format is name@example.com.");
            }
        }

        // Get valid password input
        while (true) {
            System.out.print("Password: ");
            password = sc.nextLine();
            if (isValidPassword(password)) {
                break;
            } else {
                System.out.println("Invalid password. The password must contain at least one special character.");
            }
        }

        // Hash the password before saving it to the array and database
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); 
        //hashedPassword = loginPasswordHashing.hashPassword(password);

        names[userCount[0]] = name;
        emails[userCount[0]] = email;
        passwords[userCount[0]] = hashedPassword;
        userCount[0]++;

        saveUserToDatabase(name, email, hashedPassword); // Save hashed password to transaction.db
        System.out.println();
        LedgerSystem.mainMenu(sc, names, emails, passwords, userCount);
    }

    // Check if the name is valid (only letters, digits, or spaces)
    public static boolean isValidName(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(Character.isLetterOrDigit(c) || Character.isSpaceChar(c))) {
                return false;
            }
        }
        return true;
    }

    // Check if the email format is valid
    public static boolean isValidEmail(String email) {
        return email.contains("@") && !email.endsWith("@") && !email.startsWith(".") && !email.endsWith(".");
    }

    // Check if the password is valid (contains at least one special character and length >= 6)
    public static boolean isValidPassword(String password) {
        if (password.length() < 6) {
            return false;
        }
        boolean hasSpecialChar = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
                break;
            }
        }
        return hasSpecialChar;
    }

    // Save the user information to the database (transaction.db)
    public static void saveUserToDatabase(String name, String email, String password) {
    String sql = "INSERT INTO users(name, email, password) VALUES(?, ?, ?);";
    
    try (Connection conn = DatabaseUtil.connect();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
          pstmt.setString(1, name);
          pstmt.setString(2, email);
          pstmt.setString(3, password);
        
        pstmt.executeUpdate();
        System.out.println("\nRegister Successful!!!");
        
    } catch (SQLException e) {
        if (e.getMessage().contains("UNIQUE constraint failed")) {
            System.out.println("Email already exists. Please use a different email.");
        } else {
            System.out.println("Error while saving user: " + e.getMessage());
        }
    }
}

    
    // Login the user by validating the email and password
    public static void login(Scanner sc, String[] names, String[] emails, String[] passwords, int[] userCount) throws IOException {
        if (userCount[0] == 0) {
            System.out.println("\nYou have not registered yet. Please register first.");
            register(sc, names, emails, passwords, userCount);
            return;
        }
        System.out.println("\n== Please enter your email and password ==");
        sc.nextLine();

        while (true) {
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            // Query the database to get the hashed password for the provided email
            String sql = "SELECT * FROM users WHERE email = ?;";
            try (Connection conn = DatabaseUtil.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email); // Set email parameter
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Retrieve stored hashed password from the database
                    String storedHashedPassword = rs.getString("password");

                    // Compare the entered password with the stored hashed password
                    if (BCrypt.checkpw(password, storedHashedPassword)) {
                        int user_id = rs.getInt("user_id");
                        System.out.println("\nLogin Successful!!!");
                        Reminder_System_Loan_Repayment.remindUser(user_id);
                        LedgerSystem.subMenu(sc, rs.getString("name")); // Call submenu with user's name
                        break; // Exit the loop after successful login
                    } else {
                        System.out.println("Invalid email or password.");
                    }
                } else {
                    System.out.println("Invalid email or password.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
    // Password hashing and verification utility class
    public static class loginPasswordHashing {
        public static String hashPassword(String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt()); 
    }
        public static boolean checkPassword(String candidate, String hashed) {
            return BCrypt.checkpw(candidate, hashed);
    }
}
    public static void registerShutdownHook() {
        // Registering the shutdown hook to reset data on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nResetting user data on shutdown...");
            start.resetData(); // Reset both in-memory and database data
        }));
    }
}
