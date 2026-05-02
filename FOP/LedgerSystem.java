package fop_pasti_assignment;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LedgerSystem {
    
    static int user_id = -1;
    static double balance = 0.0;
    static double totalRepayment = 0.0;
    
    //1. Registration and Login
    public static void main(String[] args) throws IOException {
        start.resetData();
        start.createTables();
        PasswordHashing.registerShutdownHook();
        Scanner sc = new Scanner(System.in);
        String [] emails = new String[5];
        String [] passwords = new String[5];
        String [] names = new String [5];
        int [] userCount ={0}; // To keep track the number of registered users
        
        while(true){
            System.out.println("\n== Ledger System ==");
            mainMenu(sc, names, emails, passwords, userCount);
        }    
    } 
    
    public static int mainMenu(Scanner sc,String[] names, String[] emails, String[] passwords, int[] userCount) throws IOException{    
        System.out.println("Login or Register: \n1. Login \n2. Register \n");
        System.out.print(">");
        int choice = sc.nextInt();
        switch (choice) {
            case 1 -> PasswordHashing.login(sc, names, emails, passwords, userCount);
            case 2 -> PasswordHashing.register(sc, names, emails, passwords, userCount);
            default -> System.out.println("Invalid choice. Please select 1 or 2.");
        }
        return choice;
    }
    
    public static int subMenu(Scanner sc, String name) throws IOException, SQLException{
        int transactionCount = 0; // To keep track the number of transaction
        String[] transactionDates = new String[100];
        String[] descrs = new String[100];
        double[] debits = new double[100];
        double[] credits = new double[100];
        // Get user_id
        String getUserQuery = "SELECT user_id FROM Users WHERE name = ?";

        try (Connection conn = DatabaseUtil.connect(); 
            PreparedStatement getUserStmt = conn.prepareStatement(getUserQuery)) {
    
        // Get user_id based on the name
        getUserStmt.setString(1, name);
        try (ResultSet rs = getUserStmt.executeQuery()) {
            if (rs.next()) {
                user_id = rs.getInt("user_id");
            } else {
            System.out.println("Error: User not found for name: " + name);
            }
        }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } 
        
        while(true){
            System.out.println("\n== Welcome, " + name + " ==");
            View_Account_Balance_History.displayBalance(balance);
            System.out.printf("\nSavings: %.2f", Savings.savings);
            System.out.printf("\nLoan: %.2f", totalRepayment);
            System.out.println();
            System.out.println("\n== Transaction ==");
            System.out.println("1. Debit \n2. Credit \n3. History \n4. Savings \n5. Credit Loan \n6. Deposit Interest Predictor \n7. Logout \n8. Chart");
            System.out.print("\n>");
            int choice = sc.nextInt();
            switch(choice){
                case 1:
                    if (!LedgerLoan.checkLoanStatus(user_id)) {
                        System.out.println("\nYour loan is overdue! Debit and credit transactions are not allowed!");}
                    else{
                        balance = Record_Debit_and_Credit.Debit(sc, balance, transactionDates, descrs, debits, credits, transactionCount,user_id);
//                        Record_Debit_and_Credit.createTable();
                        transactionCount++;}
                    break;
                case 2:
                    if (!LedgerLoan.checkLoanStatus(user_id)) {
                        System.out.println("\nYour loan is overdue! Debit and credit transactions are not allowed!");}
                    else{
                        balance = Record_Debit_and_Credit.Credit(sc, balance, transactionDates, descrs, debits, credits, transactionCount, user_id);
//                        Record_Debit_and_Credit.createTable();
                        transactionCount++;}
                    break;
                case 3:
                    View_Account_Balance_History.displayTransactionHistory(user_id);
                    Filter_Sorting_History.History(sc, transactionDates, descrs, debits, credits, transactionCount);
                    break;
                case 4:
                    Savings.activateSavings(sc, user_id);
                    break;
                case 5:
                    LedgerLoan.chooseLoan(sc, user_id);
                    break;
                case 6:
                    Deposit_Interest_Predictor.DIP();
                    break;
                case 7:
                    System.out.println("\nThank you for using 'Ledger System'. ");
                    return choice;
                case 8:
                    chart.chartt();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}