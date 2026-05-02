package fop_pasti_assignment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Deposit_Interest_Predictor {
    private int bankId;
    private String bankName;
    private double interestRate;

    public Deposit_Interest_Predictor(int bankId, String bankName, double interestRate) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.interestRate = interestRate;
    }

    public int getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public static void displayBanks(List<Deposit_Interest_Predictor> banks) {
        System.out.println("\n== Deposit Interest Predictor ==");
         for (int i = 0; i < banks.size(); i++) {
            Deposit_Interest_Predictor bank = banks.get(i);
            System.out.printf("%d. %s - %.2f%%\n", bank.getBankId(), bank.getBankName(), bank.getInterestRate());
        }
    }

    public static double calculateMonthlyInterest(double deposit, double interestRate) {
        return (deposit * interestRate / 100) / 12;
    }

    public static void setupDatabase() {
        String deleteDataSQL = "DELETE FROM bank;";
        String resetAutoIncrementSQL = "DELETE FROM sqlite_sequence WHERE name='bank';";
        String sql = "CREATE TABLE IF NOT EXISTS bank ("
                + " bank_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " bank_name TEXT NOT NULL,"
                + " interest_rate DECIMAL NOT NULL"
                + ");";
        String insertDataSQL = """
                INSERT OR IGNORE INTO bank (bank_name, interest_rate)
                VALUES
                ('RHB', 2.6),
                ('Maybank', 2.5),
                ('Hong Leong', 2.3),
                ('Alliance', 2.85),
                ('AmBank', 2.55),
                ('Standard Chartered', 2.65);
            """;

        try (Connection conn = DatabaseUtil.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(deleteDataSQL);
            stmt.execute(resetAutoIncrementSQL);
            stmt.execute(sql);
            stmt.execute(insertDataSQL);
        } catch (SQLException e) {
            System.out.println("Database Setup Error: " + e.getMessage());
        }
    }

    public static List<Deposit_Interest_Predictor> getAllBanks() throws SQLException {
        List<Deposit_Interest_Predictor> banks = new ArrayList<>();
        String sql = "SELECT bank_id, bank_name, interest_rate FROM bank";
        try (Connection conn = DatabaseUtil.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                        int bankId = rs.getInt("bank_id");
                        String bankName = rs.getString("bank_name");
                        double interestRate = rs.getDouble("interest_rate");
                        banks.add(new Deposit_Interest_Predictor(bankId, bankName, interestRate));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return banks;
    }
    }
            

    public static void DIP() throws SQLException {
        setupDatabase(); // Initialize database
        List<Deposit_Interest_Predictor> banks = getAllBanks(); // Fetch banks

        if (banks.isEmpty()) {
            System.out.println("No banks available in the database.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        displayBanks(banks);

        System.out.print("\nEnter your deposit: ");
        double deposit = sc.nextDouble();

        System.out.print("Please choose a bank: ");
        int bankChoice = sc.nextInt();

        Deposit_Interest_Predictor selectedBank = null;
         for (int i = 0; i < banks.size(); i++) {
            Deposit_Interest_Predictor bank = banks.get(i);
            if (bank.getBankId() == bankChoice) {
                selectedBank = bank;
                break;
            }
        }
        
        if (selectedBank != null) {
            double interest = calculateMonthlyInterest(deposit, selectedBank.getInterestRate());
            System.out.printf("\nThe earned interest from %s is: RM%.2f\n", selectedBank.getBankName(), interest);
        } else {
            System.out.println("Invalid bank selection.");
        }

    }
}
