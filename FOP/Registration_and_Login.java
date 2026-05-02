package fop_pasti_assignment;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Registration_and_Login {

    public static void register(Scanner sc,String[] names, String[] emails, String[] passwords, int[] userCount) throws IOException{
        System.out.println("\n== Please fill in the form ==");
        sc.nextLine();
        
        String name = "";
        String email = "";
        String password = "";
        
        while(true){
            System.out.print("Name: ");
            name = sc.nextLine();
            if(isValidName(name)){
                break;
            }else{
                System.out.println("Invalid name.");
            }    
        }
        
        while(true){
            System.out.print("Email: ");
            email = sc.nextLine();
            if(isValidEmail(email)){
                break;
            }else{
                System.out.println("Invalid email. The correct format is name@example.com.");
            }
        }
        
        while(true){
            System.out.print("Password: ");
            password = sc.nextLine();
            if(isValidPassword(password)){
                break;
            }else{
                System.out.println("Invalid password. The password must contain at least one special character.");
                }     
            }
        
        names[userCount[0]] = name;
        emails[userCount[0]] = email;
        passwords[userCount[0]] = password;
        userCount[0]++;
        
        saveUserToDatabase(name,email,password);
        System.out.println();
        LedgerSystem.mainMenu(sc, names, emails, passwords, userCount);
    }
    public static boolean isValidName(String name){
        for(int i=0; i < name.length(); i++){
            char c = name.charAt(i);
            if(!(Character.isLetterOrDigit(c) || Character.isSpaceChar(c))){
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidEmail(String email){
        if(!email.contains("@") || !email.contains(".")){
            return false;
        }else if(email.startsWith("@") || email.endsWith("@") || email.startsWith(".") || email.endsWith(".")){
            return false;
        }
        return true;
    }
    
    public static boolean isValidPassword(String password){
        if(password.length()< 6){
            return false;
        }
        boolean hasSpecialChar = false;
        for(int i=0; i< password.length(); i++){
           char c = password.charAt(i);
           if(!Character.isLetterOrDigit(c)){
               hasSpecialChar = true;
               break;
           }
        }
        return hasSpecialChar;
    }
    
    public static void saveUserToDatabase(String name, String email, String password){
        String sql = "INSERT INTO users(name, email, password) VALUES(?, ?, ?);";
        try (Connection conn = DatabaseUtil.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            System.out.println("\nRegister Successful!!!");
        } catch (SQLException e) {
            System.out.println("Email already exists.");
        }
    }
    public static void login(Scanner sc,String[] names, String[] emails, String[] passwords, int[] userCount) throws IOException{
        if(userCount[0] == 0){
            System.out.println("\nYou have not registered yet. Please register first.");
            register(sc, names, emails, passwords, userCount);
            return;
        }
        System.out.println("\n== Please enter your email and password ==");
        sc.nextLine();
        
        while(true){
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();
            
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?;";
            try (Connection conn = DatabaseUtil.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
        
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int user_id = rs.getInt("user_id");
                        System.out.println("\nLogin Successful!!!");
                        Reminder_System_Loan_Repayment.remindUser(user_id);
                        LedgerSystem.subMenu(sc, rs.getString("name")); // Using the name directly
                        break; // Exit the loop on successful login
                } else {
                    System.out.println("Invalid email or password.");
                }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

