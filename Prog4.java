import java.sql.*;
import java.util.Scanner;

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
                    // Insert a new member into the database
                    break;
                case 2:
                    // Delete a member from the database
                    break;
                case 3:
                    // Insert a new course into the database
                    break;
                case 4:
                    // Delete a course from the database
                    break;
                case 5:
                    // Add, update, or delete a course package
                    break;
                // ....
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
    private static void insertMember(Statement stmt) throws SQLException {
        // Implement logic to get member information from the user and insert it into
        // the database
        // Update related records based on the selected course package
        // Make sure to handle any exceptions that may occur during the insertion
        // process
    }

    // Add a new method for record deletion
    private static void deleteMember(Statement stmt) throws SQLException {
        // Implement logic to delete a member, considering checks for unreturned
        // equipment,
        // unpaid balances, and active course participation
        // Make sure to handle any exceptions that may occur during the deletion process
    }

    // Add a new method for course insertion
    private static void insertCourse(Statement stmt) throws SQLException {
        // Implement logic to get course information from the user and insert it into
        // the database
        // Make sure to handle any exceptions that may occur during the insertion
        // process
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
