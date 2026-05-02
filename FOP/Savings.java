package fop_pasti_assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDate;

public class Savings {
    static double savings = 0.0;
    static boolean savingsActivated = false;
    static double savingsPercentage = 0.0;

    public static void activateSavings(Scanner sc, int user_id) {
        System.out.println("\n== Savings Activation ==");
        System.out.print("\nAre you sure you want to activate it? (Y/N): ");
        char activation = sc.next().charAt(0);

        switch (activation) {
            case 'Y', 'y' -> {
                System.out.print("Please enter the percentage you wish to deduct from the next debit: ");
                double percentage = sc.nextDouble();
                if (percentage < 0 || percentage > 100) {
                    System.out.println("Invalid percentage. Savings activation failed.");
                } else {
                    savingsActivated = true;
                    savingsPercentage = percentage;
                    String status ="";
                    String date = LocalDate.now().toString();
                    if (!savingsActivated)
                        status = "inactive";
                    else
                        status = "active";
                    saveSavingsToDatabase(user_id, status, savingsPercentage, date);
                    System.out.println("\nSavings settings added successfully!");
                }
            }
            case 'N', 'n' -> System.out.println("Savings is not activated.");
            default -> System.out.println("Invalid input. Savings activation canceled.");
        }
    }

    public static double applySavings(double amount) {
        if (savingsActivated) {
            double savingsAmount = (savingsPercentage / 100) * amount;
            savings += savingsAmount;
            amount -= savingsAmount;
        }
        return amount;
    }

    public static double savingsTransfer(String transaction_date, double balance, int user_id) {
        String[] parts = transaction_date.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        boolean isEndOfMonth = false;

        if (month == 2) { // February
            if (isLeapYear(year)) {
                isEndOfMonth = (day == 29);
            } else {
                isEndOfMonth = (day == 28);
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) { // Months with 30 days
            isEndOfMonth = (day == 30);
        } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) { // Months with 31 days
            isEndOfMonth = (day == 31);
        }

        if (isEndOfMonth) {
            System.out.println("\nEnd of month detected. Transferring savings to balance...");
            balance += savings;
            savings = 0.0;
            System.out.printf("Savings transferred! New Balance: %.2f\n", balance);
        } else {
            System.out.println("Not the end of the month. No savings transfer.");
        }

        return balance;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // Method to save to the database
    private static void saveSavingsToDatabase(int user_id, String Status, double Percentage, String Date) {
        String sql = "INSERT INTO savings (user_id, Status, Percentage, Date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user_id);
            pstmt.setString(2, Status);
            pstmt.setDouble(3, Percentage);
            pstmt.setString(4, Date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving savings: " + e.getMessage());
        }
    }
}
