package fop_pasti_assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LedgerLoan {
    
    static int overdueMonth = 0;
    static double monthlyInstallment = 0;
    static String transactionDate;

    public static void chooseLoan(Scanner sc, int user_Id) {
        // Initialize database and create tables if they do not exist
//        createLoansTable();
        sc = new Scanner(System.in);
        int userId = 1;  
        boolean exit = false;

        while (!exit) {
            System.out.println("\n== Credit Loan System ==");
            System.out.println("1. Apply for Loan");
            System.out.println("2. Repay Loan");
            System.out.println("3. Check Loan Status");
            System.out.println("4. Exit");
            System.out.print("\nEnter your choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> // Apply for a loan
                    applyLoan(sc, userId);
                case 2 -> // Repay an existing loan
                    repayLoan(sc, userId);
                case 3 -> {
                    // Check loan status
                    boolean status = checkActiveStatus(userId);
                    if (status) {
                        System.out.println("\nLoan status is active and in good standing.");
                    }else
                        System.out.println("\nLoan status is inactive.");
                }
                case 4 -> exit = true;
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    // Apply for Loan
    public static void applyLoan(Scanner sc, int userId) {
        if (checkActiveStatus(userId)) {
            System.out.println("You already have an active loan. Please repay it before applying for a new one.");
            return;
        }

        System.out.println("\n== Credit Loan == \nApply for Loan");
        System.out.print("Please enter the principal amount: ");
        double principal = sc.nextDouble();
        System.out.print("Please enter the annual interest rate in %: ");
        double interest = sc.nextDouble();
        System.out.print("Please enter the repayment period in months: ");
        int period = sc.nextInt();

        // Validate inputs
        if (principal <= 0) {
            System.out.println("Principal amount must be greater than 0.");
            return;
        }
        if (interest < 0) {
            System.out.println("Interest rate cannot be negative.");
            return;
        }
        if (period <= 0) {
            System.out.println("Repayment period must be greater than 0.");
            return;
        }

        // Calculate repayment details
        double monthlyInterest = (interest / 100) / 12;
        double totalRepayment = principal * Math.pow((1 + monthlyInterest), period);
        monthlyInstallment = totalRepayment / period;
        transactionDate = LocalDate.now().toString();

        System.out.printf("\nThe total repayment is: %.2f", totalRepayment);
        System.out.printf("\nThe monthly installment is: %.2f", monthlyInstallment);
        System.out.println("\nYou should make your repayment by " + getRepaymentDate() + ".\n");

        totalRepayment = Math.round(totalRepayment * 100.0) / 100.0;

        String sql = "INSERT INTO Loans (user_id, principal_amount, interest_rate, repayment_period, "
                + "outstanding_balance, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, principal);
            pstmt.setDouble(3, interest);
            pstmt.setInt(4, period);
            pstmt.setDouble(5, totalRepayment);
            pstmt.setString(6, "active");
            pstmt.setString(7, transactionDate);
            pstmt.executeUpdate();
            System.out.println("\nLoan application successful!");
        } catch (SQLException e) {
            System.out.println("Failed to apply for the loan: " + e.getMessage());
        }
    }

    // Repay Loan
    public static void repayLoan(Scanner sc, int userId) {
        if (!checkLoanStatus(userId)) {
            System.out.println("\nYou do not have any active loan to repay.");
            return;
        }

        String query = "SELECT loan_id, outstanding_balance, repayment_period FROM Loans "
                + "WHERE user_id = ? AND status = 'active';";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int loanId = rs.getInt("loan_id");
                    double outstandingBalance = rs.getDouble("outstanding_balance");
                    int remainingPeriod = rs.getInt("repayment_period");

                    System.out.printf("\nThe total outstanding balance is: %.2f.", outstandingBalance);
                    System.out.printf("\nThe monthly installment is: %.2f.", monthlyInstallment);

                    System.out.print("\nPlease enter the amount you wish to repay: ");
                    double repayment = sc.nextDouble();

                    // Validate repayment amount
                    double requiredRepayment = outstandingBalance / remainingPeriod;
                    if (repayment <= 0) {
                        System.out.println("Repayment amount must be greater than zero.");
                        return;
                    } else if (repayment < requiredRepayment) {
                        System.out.println("The repayment amount is too low. Please pay at least the required amount.");
                        return;
                    } else if (repayment > outstandingBalance) {
                        System.out.println("The repayment exceeds the outstanding balance. Auto adjusting to full repayment.");
                        repayment = outstandingBalance;
                    }

                    // Update outstanding balance
                    outstandingBalance -= repayment;
                    
                    if(outstandingBalance < 0)
                        outstandingBalance = 0;
                    // If outstanding balance is zero or less, mark the loan as repaid
                    String status = "active";
                    if ( outstandingBalance == 0)
                        status = "repaid";
                

                    // Update loan status and details in the database
                    String updateSql = "UPDATE Loans SET outstanding_balance = ?, status = ? WHERE loan_id = ?";
                    try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                        updatePstmt.setDouble(1, outstandingBalance); // Ensure balance does not go negative
                        updatePstmt.setString(2, status);
                        updatePstmt.setInt(3, loanId);
                        updatePstmt.executeUpdate();
                    }

                    // Display repayment confirmation
                    System.out.printf("\nRepayment of %.2f successful!", repayment);
                    if (outstandingBalance <= 0) {
                        System.out.println(" Your loan has been fully repaid!");
                    }
                } else {
                    System.out.println("\nNo active loans found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to process the repayment: " + e.getMessage());
        }
    }

    public static String getRepaymentDate(){
        LocalDate date = LocalDate.parse(transactionDate);
        return date.plusMonths(1).toString();
    }

    // Check Loan Status
    public static boolean checkLoanStatus(int userId) {
        String query = "SELECT loan_id, outstanding_balance, created_at FROM Loans " +
                       "WHERE user_id = ? AND status = 'active';";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double outstandingBalance = rs.getDouble("outstanding_balance");
                    LocalDate loanDate = LocalDate.parse(rs.getString("created_at").split(" ")[0]);
                    long monthsSinceLoan = ChronoUnit.MONTHS.between(loanDate, LocalDate.now());

                    if (outstandingBalance > 0) {
                        if (monthsSinceLoan > 0) {
                            overdueMonth = (int) monthsSinceLoan;
                            System.out.println("Your loan is overdue by " + overdueMonth + " months!");
                            return false; // Loan is overdue
                        }
                        return true; // Loan is active and in good standing
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check loan status: " + e.getMessage());
        }
        return true; // No active loan found
}
    
    public static boolean checkActiveStatus(int userId){
        String query = "SELECT loan_id FROM Loans WHERE user_id = ? AND status = 'active';";

        try (Connection conn = DatabaseUtil.connect();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true; // Active loan exists
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check active loan status: " + e.getMessage());
        }
        return false; // No active loan found
    }
}


