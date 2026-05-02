package fop_pasti_assignment;

import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class View_Account_Balance_History {
    public static void displayBalance(double balance) {
        System.out.printf("\nBalance: %.2f", balance);
}

   public static void displayTransactionHistory(int userId) throws IOException {
    String sql = "SELECT transaction_id, transaction_type, amount, description, date FROM transactions WHERE user_id = ? ORDER BY date DESC";
    String fileName = "transaction_history_user_" + userId + ".csv";

    System.out.println("\n== History ==");
    System.out.printf("%-12s %-20s %-12s %-12s %-15s%n", "Date", "Description", "Debit", "Credit", "Balance");

    try (Connection conn = DatabaseUtil.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, userId); // Set the user_id parameter for the query
        ResultSet rs = pstmt.executeQuery();

        // Create CSV file and write headers
        try (FileWriter fileWriter = new FileWriter(fileName);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println("Transaction ID,Transaction Type,Amount,Description,Date");

            double currentBalance = 0.0;

            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                double amount = rs.getDouble("amount");
                String transactionType = rs.getString("transaction_type").toLowerCase();
                String description = rs.getString("description");
                String date = rs.getString("date");

                // Separate debit and credit amounts
                double debit = 0.0;
                double credit = 0.0;

                if ("debit".equalsIgnoreCase(transactionType)) {
                    debit = amount;
                    currentBalance += debit; // Reduce balance for debit
                } else if ("credit".equalsIgnoreCase(transactionType)) {
                    credit = amount;
                    currentBalance -= credit; // Increase balance for credit
                }

                // Print the transaction details with the running balance
                System.out.printf("%-12s %-20s %-12.2f %-12.2f %-15.2f%n",
                        date,
                        description,
                        debit,
                        credit,
                        currentBalance);

                // Write transaction details to CSV
                printWriter.printf("%d,%s,%.2f,%s,%s%n",
                        transactionId,
                        transactionType,
                        amount,
                        description,
                        date);
            }

            System.out.println("\nFile exported!");

        } catch (IOException e) {
            System.out.println("Error while writing CSV: " + e.getMessage());
        }
    } catch (SQLException e) {
        System.out.println("Error fetching transaction history: " + e.getMessage());
    }
} 
}
