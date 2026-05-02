package fop_pasti_assignment;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reminder_System_Loan_Repayment {

    public static void remindUser(int userId) {
        String query = "SELECT loan_id, principal_amount, repayment_period, created_at, outstanding_balance, status " +
                       "FROM loans " +
                       "WHERE user_id = ? AND status = 'active';";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int loanId = rs.getInt("loan_id");
                    int repaymentPeriod = rs.getInt("repayment_period");
                    String createdAt = rs.getString("created_at");

                    // Calculate due date
                    LocalDate loanStartDate = LocalDate.parse(createdAt);
                    LocalDate dueDate = loanStartDate.plusMonths(repaymentPeriod);
                    LocalDate today = LocalDate.now();

                    long daysRemaining = ChronoUnit.DAYS.between(today, dueDate);

                    if (daysRemaining <= 7 && daysRemaining >= 0) {
                        System.out.println("Reminder: Loan ID " + loanId + " has a repayment due in " + daysRemaining + " days.");
                    } else if (daysRemaining < 0) {
                        System.out.println("Loan ID " + loanId + " is overdue by " + Math.abs(daysRemaining) + " days.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}

