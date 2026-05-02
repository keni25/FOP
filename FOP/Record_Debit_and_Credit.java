package fop_pasti_assignment;
import static fop_pasti_assignment.Savings.savingsActivated;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDate;

public class Record_Debit_and_Credit {
    private static String transaction_date;
    public static void saveUserToDatabase(int user_id, double amount, String descr, String transaction_type, String transaction_date) {
    String sql = "INSERT INTO transactions (user_id, transaction_type, amount, description, date) VALUES (?, ?, ?, ?, ?);";
    try (Connection conn = DatabaseUtil.connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, user_id); // Set user_id
        pstmt.setString(2, transaction_type); // Set transaction_type
        pstmt.setDouble(3, amount); // Set amount
        pstmt.setString(4, descr); // Set description
        pstmt.setString(5, transaction_date);
        pstmt.executeUpdate();
        //System.out.println("\n" + transaction_type + " Successfully Recorded!!!");
    } catch (SQLException e) {
        System.out.println("Error: Failed to record transaction. " + e.getMessage());
    }
}
    public static double Debit(Scanner sc, double balance, String[] transactionDates, String[] description, double[] debits, double[] credits, int transactionCount, int user_id){
        double amount;
        double initialAmount; // Store the original debit amount for History part
        String descr;
        System.out.println("\n== Debit ==");
        
        // Validate the transaction amount
        while(true){
            System.out.print("Enter amount: ");
            amount = sc.nextDouble();
            if(amount<=0 || amount>1000000){
                System.out.println("Please enter a valid amount.");
            }else{
                break;
            }  
        }    
        sc.nextLine();
        initialAmount = amount;
        
        // Validate transaction description
        while(true){
            System.out.print("Enter description: ");
            descr = sc.nextLine();
                if(descr.length()> 100){
                    System.out.println("The description should not exceed 100 characters.");
                }else{
                    break;
                }    
        }
                amount = Savings.applySavings(amount);

        // Store transaction information
        description[transactionCount] = descr;
        debits[transactionCount] =initialAmount; // Print the original amount for History part
        credits[transactionCount] = 0;
        String transaction_type = "debit";
        saveUserToDatabase(user_id, amount, descr, transaction_type, LocalDate.now().toString());
        transaction_date = LocalDate.now().toString();
        transactionDates[transactionCount] = transaction_date;
        validateDate(transaction_date);
        
        
        balance = balance + amount;
        System.out.println("\nDebit Successfully Recorded!!!");
        
        // Activate SavingsTransfer 
        if(savingsActivated){
            balance = Savings.savingsTransfer(transaction_date, balance, user_id);
            return balance;}
        return balance;
    }
    
    public static double Credit(Scanner sc, double balance, String[] transactionDates, String[] description, double[] debits, double[] credits, int transactionCount, int user_id){      
        double amount2;
        String descr;
        System.out.println("\n== Credit ==");
        
        // Validate the transaction amount
        while(true){
            System.out.print("Enter amount: ");
            amount2 = sc.nextDouble();
            if(amount2<=0 || amount2>1000000){
                System.out.println("Please enter a valid amount.");
            }else{
                break;
            }  
        }    
        sc.nextLine();
        
        // Validate transaction description
        while(true){
            System.out.print("Enter description: ");
            descr = sc.nextLine();
                if(descr.length()> 100){
                    System.out.println("The description should not exceed 100 characters.");
                }else{
                    break;
                }    
        }
        description[transactionCount] = descr;
        debits[transactionCount] = 0;
        credits[transactionCount] = amount2;
        transaction_date = LocalDate.now().toString();
        transactionDates[transactionCount] = transaction_date;
        validateDate(transaction_date);
        
        String transaction_type = "credit";
        saveUserToDatabase(user_id, amount2, descr, transaction_type, LocalDate.now().toString());
        balance = balance - amount2;
        System.out.println("\nCredit Successfully Recorded!!!");
        return balance;
    }
    
    public static void validateDate(String transaction_date){
        String date = transaction_date;
        String[] parts = date.split("-");
        //Validate the year (4 digits)
        String year = parts[2];
            // Check if the year is 2025
            String currentYear = "2025";
            if(year.compareTo(currentYear) > 0){
                System.out.println("The transaction year cannot be in the future.");
            }
    }
   
}
