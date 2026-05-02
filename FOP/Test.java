//package fop_pasti_assignment;
//private static void initializeDatabase(Connection conn) throws SQLException {
//    String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
//            + "id INTEGER PRIMARY KEY,"
//            + "description TEXT,"
//            + "amount REAL,"
//            + "transaction_type TEXT"
//            + ");";
//    String createSavingsTable = "CREATE TABLE IF NOT EXISTS savings ("
//            + "id INTEGER PRIMARY KEY,"
//            + "date TEXT,"
//            + "percentage REAL"
//            + ");";
//    String createLoansTable = "CREATE TABLE IF NOT EXISTS loans ("
//            + "id INTEGER PRIMARY KEY,"
//            + "created_at TEXT,"
//            + "outstanding_balance REAL"
//            + ");";
//    
//    try (Statement stmt = conn.createStatement()) {
//        stmt.execute(createTransactionsTable);
//        stmt.execute(createSavingsTable);
//        stmt.execute(createLoansTable);
//    }
//}
//
//private static Connection connectToDatabase() {
//    try {
//        Connection conn = DriverManager.getConnection("jdbc:sqlite:Users0.db");
//        initializeDatabase(conn);
//        return conn;
//    } catch (SQLException e) {
//        System.out.println("Database connection error: " + e.getMessage());
//        return null;
//    }
//}
