import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Prog4 {
    public static void main(String[] args) {

        final String oracleURL = // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        String username = null, // Oracle DBMS username
                password = null; // Oracle DBMS password

        if (args.length == 2) { // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage:  java JDBC <username> <password>\n"
                    + "    where <username> is your Oracle DBMS"
                    + " username,\n    and <password> is your Oracle"
                    + " password (not your system password).\n");
            System.exit(-1);
        }

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {

            Class.forName("oracle.jdbc.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);

        }

        // make and return a database connection to the user's
        // Oracle database
        Connection dbconn = null;
        try {
            dbconn = DriverManager.getConnection(oracleURL, username, password);

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        Statement stmt = null;
        try {
            stmt = dbconn.createStatement();
            // dbconn.setAutoCommit(false);
            // answer = stmt.executeQuery(query);

            int choice = getNextAction();
            // Get user input from getNextAction()
            switch (choice) {
                case 1:
                    insertMember(dbconn);
                    break;
                case 2:
                    deleteMember(dbconn);
                    break;
                case 3:
                    insertCourse(stmt);
                    break;
                case 4:
                    // Delete a course from the database
                    break;
                case 5:
                    // Add, update, or delete a course package
                    break;
            }

            // The queries that the application is to be able to answer:
            int queryId = getNextQuery();
            switch (queryId) {
                case 1:
                    getMembersNegBalance(stmt);
                    break;
                case 2:
                    getMemberScheduleNov(stmt);
                    break;
                case 3:
                    getTrainersScheduleDec(stmt);
                    break;
                case 4:
                    // TODO:
                    break;
                case 5: 
            }

            // Shut down the connection to the DBMS.
            stmt.close();
            dbconn.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
    }

    private static int getNextAction() {
        Scanner sc = new Scanner(System.in);
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║   1. Insert a new member                                    ║");
        System.out.println("║   2. Delete a member                                        ║");
        System.out.println("║   3. Insert a new course                                    ║");
        System.out.println("║   4. Delete a course                                        ║");
        System.out.println("║   5. Manage course packages                                 ║");
        System.out.println("║   6. Quit the program.                                      ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        int choice = sc.nextInt();
        return choice;
    }

    private static int getNextQuery() {
        Scanner sc = new Scanner(System.in);
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║   1. Get all Members with Negative Balance                  ║");
        System.out.println("║   2. Get a Member's schedule for November                   ║");
        System.out.println("║   3. Get all Trainers' schedule for December                ║");
        System.out.println("║   4. Optional Query                                         ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        int choice = sc.nextInt();
        return choice;
    }

    // Add a new method for record insertion
private static void insertMember(Connection dbconn) throws SQLException {   
    Scanner sc = new Scanner(System.in);

    // Prompt for member information
    System.out.print("Enter member name: ");
    String memberName = sc.nextLine();

    System.out.print("Enter member phone number: ");
    String memberPhone = sc.nextLine();

    // Fetch the highest existing member ID and increment it
    int memberId = getNextMemberId(dbconn);

    // Insert the member into the database
    String insertSql = "INSERT INTO Member (id, name, phone, membershipLevel, totalSpending) VALUES (?, ?, ?, 'Regular', 0.0)";
    PreparedStatement pstmt = dbconn.prepareStatement(insertSql);

    pstmt.setInt(1, memberId);
    pstmt.setString(2, memberName);
    pstmt.setString(3, memberPhone);

    int rowsAffected = pstmt.executeUpdate();
    if (rowsAffected > 0) {
        System.out.println("Member added successfully.");
        //printAllMembers(dbconn);
        displayPackagesWithCourses(dbconn,memberId);
        // Display available packages and handle package selection
    } else {
        System.out.println("Error: Member could not be added.");
    }

    pstmt.close();
}

private static void displayPackagesWithCourses(Connection dbconn, int memberId) throws SQLException {
    String packageQuery = "SELECT name, price FROM Package ORDER BY name";
    Statement packageStmt = dbconn.createStatement();
    ResultSet packageRs = packageStmt.executeQuery(packageQuery);

    List<String> packageNames = new ArrayList<>();
    int index = 1;

    while (packageRs.next()) {
        String packageName = packageRs.getString("name");
        double packagePrice = packageRs.getDouble("price");

        if (areCoursesAvailable(dbconn, packageName)) {
            packageNames.add(packageName); // Store package names for later reference
            System.out.printf("%d: %s (Price: $%.2f)\n", index++, packageName, packagePrice);
            displayCoursesForPackage(dbconn, packageName);
        }
    }

    packageStmt.close();
    selectAndLinkPackage(dbconn, packageNames, memberId);
}
private static boolean areCoursesAvailable(Connection dbconn, String packageName) throws SQLException {
    String courseQuery = "SELECT c.maxParticipants, c.currentParticipants " +
                         "FROM Course c JOIN PackageCourse pc ON c.name = pc.courseName " +
                         "WHERE pc.packageName = ?";
    PreparedStatement courseStmt = dbconn.prepareStatement(courseQuery);
    courseStmt.setString(1, packageName);
    ResultSet courseRs = courseStmt.executeQuery();

    while (courseRs.next()) {
        int maxParticipants = courseRs.getInt("maxParticipants");
        int currentParticipants = courseRs.getInt("currentParticipants");
        if (currentParticipants >= maxParticipants) {
            courseStmt.close();
            return false; // Course is full, do not show this package
        }
    }

    courseStmt.close();
    return true; // All courses in the package have available space
}

private static void displayCoursesForPackage(Connection dbconn, String packageName) throws SQLException {
    String courseQuery = "SELECT c.name, c.currentParticipants, c.maxParticipants " +
                         "FROM Course c JOIN PackageCourse pc ON c.name = pc.courseName " +
                         "WHERE pc.packageName = ?";
    PreparedStatement courseStmt = dbconn.prepareStatement(courseQuery);
    courseStmt.setString(1, packageName);
    ResultSet courseRs = courseStmt.executeQuery();

    while (courseRs.next()) {
        String courseName = courseRs.getString("name");
        int currentParticipants = courseRs.getInt("currentParticipants");
        int maxParticipants = courseRs.getInt("maxParticipants");
        System.out.printf("    Course: %s (Available: %d/%d)\n", courseName, currentParticipants, maxParticipants);
    }
    courseStmt.close();
}

private static void printAllMembers(Connection dbconn) throws SQLException {
    String query = "SELECT * FROM Member";
    Statement stmt = dbconn.createStatement();
    ResultSet rs = stmt.executeQuery(query);

    System.out.println("ID\tName\t\tPhone\t\tMembership Level\tTotal Spending");
    System.out.println("-------------------------------------------------------------------------");
    while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        String membershipLevel = rs.getString("membershipLevel");
        double totalSpending = rs.getDouble("totalSpending");

        System.out.printf("%d\t%s\t%s\t%s\t\t%.2f\n", id, name, phone, membershipLevel, totalSpending);
    }

    stmt.close();
}

private static void selectAndLinkPackage(Connection dbconn, List<String> packageNames, int memberId) throws SQLException {
    Scanner sc = new Scanner(System.in);
    System.out.print("Select a package number: ");
    int choice = sc.nextInt();

    if (choice < 1 || choice > packageNames.size()) {
        System.out.println("Invalid choice. Please try again.");
        return;
    }

    // Fetching the memberId of the most recently added member
    // Assuming you are calling this right after adding a member

    String selectedPackageName = packageNames.get(choice - 1);
    linkMemberToPackage(dbconn, memberId, selectedPackageName);
}



private static void linkMemberToPackage(Connection dbconn, int memberId, String packageName) throws SQLException {
    String insertSql = "INSERT INTO PackageMembers (packageName, memberId) VALUES (?, ?)";
    PreparedStatement pstmt = dbconn.prepareStatement(insertSql);

    pstmt.setString(1, packageName);
    pstmt.setInt(2, memberId);

    int rowsAffected = pstmt.executeUpdate();
    if (rowsAffected > 0) {
        System.out.println("Member successfully linked to package " + packageName + ".");
        printPackageMembers(dbconn);
        updateCourseParticipants(dbconn, packageName);
        addDueTransaction(dbconn, packageName, memberId );
        printAllTransactions(dbconn);



    } else {
        System.out.println("Error: Could not link member to package.");
    }

    pstmt.close();
}
private static void updateCourseParticipants(Connection dbconn, String packageName) throws SQLException {
    String courseQuery = "SELECT courseName FROM PackageCourse WHERE packageName = ?";
    PreparedStatement courseStmt = dbconn.prepareStatement(courseQuery);
    courseStmt.setString(1, packageName);
    ResultSet courseRs = courseStmt.executeQuery();

    while (courseRs.next()) {
        String courseName = courseRs.getString("courseName");
        String updateSql = "UPDATE Course SET currentParticipants = currentParticipants + 1 WHERE name = ?";
        PreparedStatement updateStmt = dbconn.prepareStatement(updateSql);
        updateStmt.setString(1, courseName);
        updateStmt.executeUpdate();
        updateStmt.close();
    }
    courseStmt.close();
}
private static void addDueTransaction(Connection dbconn,  String packageName, int memberId) throws SQLException {
    // Fetch the package price
    String priceQuery = "SELECT price FROM Package WHERE name = ?";
    PreparedStatement priceStmt = dbconn.prepareStatement(priceQuery);
    priceStmt.setString(1, packageName);
    ResultSet priceRs = priceStmt.executeQuery();
    
    if (!priceRs.next()) {
        System.out.println("Error: Package not found.");
        return;
    }
    double packagePrice = priceRs.getDouble("price");
    priceStmt.close();

    // Generate a unique transaction ID
    int transactionId = getNextTransactionId(dbconn);

    // Insert the transaction
    String insertSql = "INSERT INTO Transaction (id, memberID, amount, transactionDate, transactionStatus) VALUES (?, ?, ?, CURRENT_DATE, 'DUE')";
    PreparedStatement insertStmt = dbconn.prepareStatement(insertSql);

    insertStmt.setInt(1, transactionId);
    insertStmt.setInt(2, memberId);
    insertStmt.setDouble(3, packagePrice);

    int rowsAffected = insertStmt.executeUpdate();
    if (rowsAffected > 0) {
        System.out.println("Due transaction added successfully for member ID " + memberId + ".");
    } else {
        System.out.println("Error: Could not add due transaction.");
    }

    insertStmt.close();
}


private static int getNextTransactionId(Connection dbconn) throws SQLException {
    String query = "SELECT MAX(id) FROM Transaction";
    Statement stmt = dbconn.createStatement();
    ResultSet rs = stmt.executeQuery(query);

    int nextId = 1; // Start from 1 if no transactions exist
    if (rs.next()) {
        nextId = rs.getInt(1) + 1; // Increment the highest ID by 1
    }

    stmt.close();
    return nextId;
}
private static void printAllTransactions(Connection dbconn) throws SQLException {
    String query = "SELECT * FROM Transaction";
    Statement stmt = dbconn.createStatement();
    ResultSet rs = stmt.executeQuery(query);

    System.out.println("ID\tMember ID\tAmount\t\tTransaction Date\tStatus\t\tType");
    System.out.println("------------------------------------------------------------------------------------");
    while (rs.next()) {
        int id = rs.getInt("id");
        int memberId = rs.getInt("memberID");
        double amount = rs.getDouble("amount");
        Date transactionDate = rs.getDate("transactionDate");
        String status = rs.getString("transactionStatus");
        String type = rs.getString("transactionType");

        System.out.printf("%d\t%d\t\t%.2f\t\t%s\t\t%s\t\t%s\n", id, memberId, amount, transactionDate, status, type);
    }

    stmt.close();
}

private static void printPackageMembers(Connection dbconn) throws SQLException {
    String query = "SELECT * FROM PackageMembers";
    Statement stmt = dbconn.createStatement();
    ResultSet rs = stmt.executeQuery(query);

    System.out.println("Package Name\tMember ID");
    System.out.println("-------------------------");
    while (rs.next()) {
        String packageName = rs.getString("packageName");
        int memberId = rs.getInt("memberId");
        System.out.printf("%s\t\t%d\n", packageName, memberId);
    }

    stmt.close();
}


private static int getNextMemberId(Connection dbconn) throws SQLException {
    String query = "SELECT MAX(id) FROM Member";
    Statement stmt = dbconn.createStatement();
    ResultSet rs = stmt.executeQuery(query);

    int nextId = 1; // Start from 1 if no members exist
    if (rs.next()) {
        nextId = rs.getInt(1) + 1; // Increment the highest ID by 1
    }

    stmt.close();
    return nextId;
}


private static void deleteMember(Connection dbconn) throws SQLException {
    Scanner sc = new Scanner(System.in);
    printAllMembers(dbconn);
    System.out.print("Enter member ID to delete: ");
    int memberId = sc.nextInt();

    // Check for unreturned equipment and mark as lost
    handleUnreturnedEquipment(dbconn, memberId);

    // Check for unpaid balances
    if (hasUnpaidBalances(dbconn, memberId)) {
        System.out.println("Member has unpaid balances. Cannot delete.");
        printUnpaidBalances(dbconn, memberId);
        return;
    }

    // Check for active course participation and remove
    handleActiveCourseParticipation(dbconn, memberId);

    // Delete member
    String deleteSql = "DELETE FROM Member WHERE id = ?";
    PreparedStatement pstmt = dbconn.prepareStatement(deleteSql);
    pstmt.setInt(1, memberId);

    int rowsAffected = pstmt.executeUpdate();
    if (rowsAffected > 0) {
        System.out.println("Member deleted successfully.");
    } else {
        System.out.println("Error: Member could not be deleted.");
    }

    pstmt.close();
}

private static void handleUnreturnedEquipment(Connection dbconn, int memberId) throws SQLException {
    String query = "SELECT equipmentName FROM Borrow WHERE memberId = ? AND returnTime IS NULL";
    PreparedStatement pstmt = dbconn.prepareStatement(query);
    pstmt.setInt(1, memberId);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
        String equipmentName = rs.getString("equipmentName");
        // Mark equipment as lost and update available quantity
        String updateSql = "UPDATE Equipment SET available = available - 1 WHERE name = ?";
        PreparedStatement updateStmt = dbconn.prepareStatement(updateSql);
        updateStmt.setString(1, equipmentName);
        updateStmt.executeUpdate();
        updateStmt.close();

        System.out.println("Equipment " + equipmentName + " marked as lost for member " + memberId);
    }

    pstmt.close();
}

private static boolean hasUnpaidBalances(Connection dbconn, int memberId) throws SQLException {
    String query = "SELECT COUNT(*) FROM Transaction WHERE memberID = ? AND transactionStatus = 'DUE'";
    PreparedStatement pstmt = dbconn.prepareStatement(query);
    pstmt.setInt(1, memberId);
    ResultSet rs = pstmt.executeQuery();

    boolean hasUnpaid = rs.next() && rs.getInt(1) > 0;
    pstmt.close();
    return hasUnpaid;
}

private static void printUnpaidBalances(Connection dbconn, int memberId) throws SQLException {
    String query = "SELECT id, amount FROM Transaction WHERE memberID = ? AND transactionStatus = 'DUE'";
    PreparedStatement pstmt = dbconn.prepareStatement(query);
    pstmt.setInt(1, memberId);
    ResultSet rs = pstmt.executeQuery();

    System.out.println("Unpaid Balances:");
    while (rs.next()) {
        int transactionId = rs.getInt("id");
        double amount = rs.getDouble("amount");
        System.out.printf("Transaction ID: %d, Amount Due: %.2f\n", transactionId, amount);
    }

    pstmt.close();
}

private static void handleActiveCourseParticipation(Connection dbconn, int memberId) throws SQLException {
    String query = "SELECT packageName FROM PackageMembers WHERE memberId = ?";
    PreparedStatement pstmt = dbconn.prepareStatement(query);
    pstmt.setInt(1, memberId);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
        String packageName = rs.getString("packageName");
        // Update course participant numbers
        updateCourseParticipantsOnMemberDeletion(dbconn, packageName);
        // Delete package member record
        deletePackageMemberRecord(dbconn, packageName, memberId);
    }

    pstmt.close();
}

private static void updateCourseParticipantsOnMemberDeletion(Connection dbconn, String packageName) throws SQLException {
    String courseQuery = "SELECT courseName FROM PackageCourse WHERE packageName = ?";
    PreparedStatement courseStmt = dbconn.prepareStatement(courseQuery);
    courseStmt.setString(1, packageName);
    ResultSet courseRs = courseStmt.executeQuery();

    while (courseRs.next()) {
        String courseName = courseRs.getString("courseName");
        String updateSql = "UPDATE Course SET currentParticipants = currentParticipants - 1 WHERE name = ?";
        PreparedStatement updateStmt = dbconn.prepareStatement(updateSql);
        updateStmt.setString(1, courseName);
        updateStmt.executeUpdate();
        updateStmt.close();
    }

    courseStmt.close();
}

private static void deletePackageMemberRecord(Connection dbconn, String packageName, int memberId) throws SQLException {
    String deleteSql = "DELETE FROM PackageMembers WHERE packageName = ? AND memberId = ?";
    PreparedStatement pstmt = dbconn.prepareStatement(deleteSql);
    pstmt.setString(1, packageName);
    pstmt.setInt(2, memberId);
    pstmt.executeUpdate();
    pstmt.close();
}


    // Add a new method for course insertion
    private static void insertCourse(Statement stmt) throws SQLException {
        // Implement logic to get course information from the user and insert it into
        // the database
        // Make sure to handle any exceptions that may occur during the insertion
        // process
        String query = buildQuery();
        ResultSet response = stmt.executeQuery(query);
        if (response != null) {
            System.out.println("Inserted a new Course entry:" + response.getMetaData());
        }

    }

    private static String buildQuery() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter course name: ");
        String courseName = sc.nextLine();
        System.out.println();

        System.out.print("Enter trainer id: ");
        int trainerID = sc.nextInt();
        System.out.println();

        System.out.print("Enter weekly class day (monday/tuesday...): ");
        String weeklyClass = sc.next();
        System.out.println();

        System.out.print("Enter start date (MM/DD/YYYY): ");
        String startDate = sc.next();
        System.out.println();

        System.out.print("Enter end date (MM/DD/YYYY): ");
        String endDate = sc.next();
        System.out.println();

        System.out.print("Enter start time (0, 1200, or 1420): ");
        String startTime = sc.next();
        System.out.println();

        System.out.print("Enter end time (0, 1200, or 1420): ");
        String endTime = sc.next();
        System.out.println();

        int currParticipants = 0;

        System.out.print("Enter max participants: ");
        int maxParticipants = sc.nextInt();
        System.out.println();

        String query = "insert into course values (" +
                "\'" + courseName + "\', " +
                trainerID + ", " +
                "\'" + weeklyClass + "\', " +
                "to_date('" + startDate + "\'," + "\'MM/DD/YYYY\'), " +
                "to_date('" + endDate + "\'," + "\'MM/DD/YYYY\'), " +
                startTime + ", " +
                endTime + ", " +
                currParticipants + ", " +
                maxParticipants + ")";
        System.out.println(query);

        return query;
    }

    // Add a new method for course deletion
    private static void deleteCourse(Statement stmt) throws SQLException {
        // Implement logic to delete a course, considering enrolled members
        // Notify members before deletion and handle any exceptions that may occur
        // during the process
    }

    // Add a new method for course package insertion/update/deletion
    private static void manageCoursePackage(Statement stmt) throws SQLException {
        // Implement logic to add, update, or delete a course package
        // Make sure to handle any exceptions that may occur during the process
    }

    /**
     * Query 1: List all members with a negative balance.
     */
    private static void getMembersNegBalance(Statement stmt) throws SQLException {
        // List all members’ names and phone numbers who now have a negative balance
        // (that is, have fees that
        // are not paid off).
    }

    /**
     * Query 2: Check and see a member’s class schedule for November.
     */
    private static void getMemberScheduleNov(Statement stmt) throws SQLException {

    }

    /**
     * Query 3: Check and see all trainers’ working hours for December.
     */
    private static void getTrainersScheduleDec(Statement stmt) throws SQLException {
    }

    /**
     * Query 4: Your additional non-trivial query.
     */
    private static void customQuery(Statement stmt, String userProvidedInfo) throws SQLException {
    }

}
