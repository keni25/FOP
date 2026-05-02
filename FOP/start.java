package fop_pasti_assignment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class start{
    public static void resetData() {
    // SQL statements to drop tables
        String dropUsersTableSql = "DROP TABLE IF EXISTS users;";
        String dropTransactionsTableSql = "DROP TABLE IF EXISTS transactions;";
        String dropSavingsTableSql = "DROP TABLE IF EXISTS savings;";
        String dropLoansTableSql = "DROP TABLE IF EXISTS loans;";

        try (Connection conn = DatabaseUtil.connect();
             Statement stmt = conn.createStatement()) {

            // Drop the users table
            stmt.executeUpdate(dropUsersTableSql);
            System.out.println("Users table dropped.");

            // Drop the transactions table
            stmt.executeUpdate(dropTransactionsTableSql);
            System.out.println("Transactions table dropped.");

            // Drop the savings table
            stmt.executeUpdate(dropSavingsTableSql);
            System.out.println("Savings table dropped.");

            // Drop the loans table
            stmt.executeUpdate(dropLoansTableSql);
            System.out.println("Loans table dropped.");

        } catch (SQLException e) {
            System.out.println("Error dropping the tables: " + e.getMessage());
        }
    }
    public static void createTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + " user_id INTEGER PRIMARY KEY,"
                + " name TEXT NOT NULL,"
                + " email TEXT NOT NULL,"
                + " password TEXT NOT NULL"
                + ");";

        String createSavingsTable = "CREATE TABLE IF NOT EXISTS savings ("
                + " Savings_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " User_id INTEGER NOT NULL,"
                + " Status TEXT NOT NULL CHECK (Status IN ('active', 'inactive')),"
                + " Percentage INTEGER NOT NULL,"
                + " date DATE NOT NULL"
                + ");";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                + " transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " user_id INTEGER NOT NULL,"
                + " transaction_type TEXT NOT NULL CHECK (transaction_type IN ('debit', 'credit')),"
                + " amount DECIMAL(10,2) NOT NULL,"
                + " description TEXT,"
                + " date DATE NOT NULL,"
                + " FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";

        String createLoansTable = "CREATE TABLE IF NOT EXISTS Loans ("
                + " loan_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " user_id INTEGER,"
                + " principal_amount REAL,"
                + " interest_rate REAL,"
                + " repayment_period INTEGER,"
                + " outstanding_balance DECIMAL (30, 2) NOT NULL,"
                + " status TEXT,"
                + " created_at TEXT,"
                + " FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ");";

        try (Connection conn = DatabaseUtil.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            System.out.println("Users table is ready.");

            stmt.execute(createSavingsTable);
            System.out.println("Savings table is ready.");

            stmt.execute(createTransactionsTable);
            System.out.println("Transactions table is ready.");

            stmt.execute(createLoansTable);
            System.out.println("Loans table is ready.");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

}
