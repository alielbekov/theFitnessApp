import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Date;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

public class Main {
    private static final String postgresURL = "jdbc:postgresql://localhost:5432/eddie";
    private static final String oracleURL = // Magic lectura -> aloe access spell
            "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

    public static void main(String[] args) {
        boolean usePostgresURL = false;

        for (String arg : args) {
            usePostgresURL = arg.contains("postgres");
            if (usePostgresURL) {
                break;
            }
        }

        String username = null; // Oracle DBMS username
        String password = null; // Oracle DBMS password

        if (!usePostgresURL && args.length == 2) {
            username = args[0];
            password = args[1];
        } else if(!usePostgresURL) {
            System.out.println("\nUsage:  java Main <username> <password>\n"
                    + "    where <username> is your Oracle DBMS"
                    + " username,\n    and <password> is your Oracle"
                    + " password (not your system password).\n");
            System.exit(-1);
        }

        // make and return a database connection to the user's
        // Oracle database
        Connection dbconn = null;
        try {
            dbconn = DriverManager.getConnection(usePostgresURL ? postgresURL : oracleURL, username, password);
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        Scanner user = new Scanner(System.in);

        Statement stmt;
        try {
            stmt = dbconn.createStatement();
            // dbconn.setAutoCommit(false);
            // answer = stmt.executeQuery(query);

            int choice = getNextAction(user);
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
            int queryId = getNextQuery(user);
            switch (queryId) {
                case 1:
                    getMembersNegBalance(stmt);
                    break;
                case 2:
                    getMemberScheduleNov(stmt, user);
                    break;
                case 3:
                    getTrainersScheduleDec(stmt, user);
                    break;
                case 4:
                    // TODO:
                    break;
            }

            // Shut down the connection to the DBMS.
            user.close();
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

    private static int getNextAction(Scanner user) {
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║   1. Insert a new member                                    ║");
        System.out.println("║   2. Delete a member                                        ║");
        System.out.println("║   3. Insert a new course                                    ║");
        System.out.println("║   4. Delete a course                                        ║");
        System.out.println("║   5. Manage course packages                                 ║");
        System.out.println("║   6. Quit the program.                                      ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        String input = user.nextLine();
        return Integer.parseInt(input);
    }

    private static int getNextQuery(Scanner user) {
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║   1. Get all Members with Negative Balance                  ║");
        System.out.println("║   2. Get a Member's schedule for November                   ║");
        System.out.println("║   3. Get all Trainers' schedule for December                ║");
        System.out.println("║   4. Optional Query                                         ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        String input = user.nextLine();
        return Integer.parseInt(input);
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

    private static String militaryTimeToRegularTime(int militaryTime) {
        if (militaryTime < 0) {
            militaryTime *= -1;
        }

        int minutes = militaryTime % 100; // maybe should be % 60;
        int hours = militaryTime / 100;

        if (minutes >= 60 || hours >= 24) {
            return null;
        }

        String suffix = hours >= 12 ? " PM" : " AM";

        if (hours >= 13) {
            hours -= 12;
        } else if (hours == 0) {
            hours = 12;
        }

        return hours + ":" + String.format("%02d", minutes) + suffix;
    }

    private static int dayOfTheWeekIndex(String dayOfTheWeek) {
        return switch (dayOfTheWeek) {
            case "sunday" -> Calendar.SUNDAY;
            case "monday" -> Calendar.MONDAY;
            case "tuesday" -> Calendar.TUESDAY;
            case "wednesday" -> Calendar.WEDNESDAY;
            case "thursday" -> Calendar.THURSDAY;
            case "friday" -> Calendar.FRIDAY;
            case "saturday" -> Calendar.SATURDAY;
            default -> -1;
        };
    }

    private static String dateToString(Calendar cal) {
        String dayOfWeek = switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY -> "Sun";
            case Calendar.MONDAY -> "Mon";
            case Calendar.TUESDAY -> "Tue";
            case Calendar.WEDNESDAY -> "Wed";
            case Calendar.THURSDAY -> "Thu";
            case Calendar.FRIDAY -> "Fri";
            case Calendar.SATURDAY -> "Sat";
            default -> throw new IllegalStateException("Unexpected value: " + cal.get(Calendar.DAY_OF_WEEK));
        };

        String month = switch (cal.get(Calendar.MONTH)) {
            case Calendar.JANUARY -> "Jan";
            case Calendar.FEBRUARY -> "Feb";
            case Calendar.MARCH -> "Mar";
            case Calendar.APRIL -> "Apr";
            case Calendar.MAY -> "May";
            case Calendar.JUNE -> "Jun";
            case Calendar.JULY -> "Jul";
            case Calendar.AUGUST -> "Aug";
            case Calendar.SEPTEMBER -> "Sep";
            case Calendar.OCTOBER -> "Oct";
            case Calendar.NOVEMBER -> "Nov";
            case Calendar.DECEMBER -> "Dec";
            default -> throw new IllegalStateException("Unexpected value: " + cal.get(Calendar.DAY_OF_WEEK));
        };

        return String.format("%s %s %02d %d", dayOfWeek, month, cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.YEAR));
    }

    /**
     * Query 2: Check and see a member’s class schedule for November.
     */
    private static void getMemberScheduleNov(Statement stmt, Scanner user) throws SQLException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Phoenix"));
        System.out.print("Name of the member: ");
        String memberName = user.nextLine();

        ResultSet rs = stmt.executeQuery(
                "select Course.name, weeklyclasstime, starttime, endtime, startdate, enddate from course " +
                        "join packagecourse on course.name = packagecourse.coursename " +
                        "join packagemembers on packagecourse.packagename = packagemembers.packagename " +
                        "join member on packagemembers.memberid = member.id and member.name = '" + memberName + "'");

        while (rs.next()) {
            String courseName = rs.getString(1);
            int weeklyClassTime = dayOfTheWeekIndex(rs.getString(2));
            int startTime = rs.getInt(3);
            int endTime = rs.getInt(4);
            Date startDate = rs.getDate(5, cal);
            Date endDate = rs.getDate(6, cal);

            if (weeklyClassTime == -1) {
                continue; // fixme: invalid data, maybe throw exception.
            }

            Date novemberStart;
            Date novemberEnd;

            cal.setTime(startDate);
            if (cal.get(Calendar.MONTH) < Calendar.NOVEMBER) {
                cal.set(Calendar.MONTH, Calendar.NOVEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                novemberStart = cal.getTime();
            } else if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                novemberStart = cal.getTime();
            } else {
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                cal.set(Calendar.MONTH, Calendar.NOVEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                novemberStart = cal.getTime();
            }

            cal.setTime(endDate);
            if (cal.get(Calendar.MONTH) < Calendar.NOVEMBER) {
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
                cal.set(Calendar.MONTH, Calendar.NOVEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                novemberEnd = cal.getTime();
            } else if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                novemberEnd = cal.getTime();
            } else {
                cal.set(Calendar.MONTH, Calendar.NOVEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                novemberEnd = cal.getTime();
            }

            System.out.println(courseName + ": ");
            java.util.Date currentDate = novemberStart;
            while (novemberEnd.after(currentDate) || novemberEnd.equals(currentDate)) {
                cal.setTime(currentDate);

                if (cal.get(Calendar.DAY_OF_WEEK) == weeklyClassTime) {
                    System.out.println("  " + dateToString(cal) + ": " + militaryTimeToRegularTime(startTime) + " - " + militaryTimeToRegularTime(endTime));
                }

                cal.add(Calendar.DATE, 1);
                Date oldDate = currentDate;
                currentDate = cal.getTime();
                cal.setTime(currentDate);
                if (cal.get(Calendar.MONTH) != Calendar.NOVEMBER) {
                    cal.setTime(oldDate);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                    cal.set(Calendar.MONTH, Calendar.NOVEMBER);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    currentDate = cal.getTime();
                }
            }
        }

        rs.close();
    }

    /**
     * Query 3: Check and see all trainers’ working hours for December.
     */
    private static void getTrainersScheduleDec(Statement stmt, Scanner user) throws SQLException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Phoenix"));
        System.out.print("Name of the trainer: ");
        String trainerName = user.nextLine();

        ResultSet rs = stmt.executeQuery("select Course.name, weeklyclasstime, starttime, endtime, startdate, enddate from course " +
                "join trainer on trainerid = trainer.id and trainer.name = '" + trainerName + "'");

        while (rs.next()) {
            String courseName = rs.getString(1);
            int weeklyClassTime = dayOfTheWeekIndex(rs.getString(2));
            int startTime = rs.getInt(3);
            int endTime = rs.getInt(4);
            Date startDate = rs.getDate(5, cal);
            Date endDate = rs.getDate(6, cal);

            if (weeklyClassTime == -1) {
                continue; // fixme: invalid data, maybe throw exception.
            }

            Date novemberStart;
            Date novemberEnd;

            cal.setTime(startDate);
            if (cal.get(Calendar.MONTH) < Calendar.DECEMBER) {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                novemberStart = cal.getTime();
            } else if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
                novemberStart = cal.getTime();
            } else {
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                novemberStart = cal.getTime();
            }

            cal.setTime(endDate);
            if (cal.get(Calendar.MONTH) < Calendar.DECEMBER) {
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                novemberEnd = cal.getTime();
            } else if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
                novemberEnd = cal.getTime();
            } else {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                novemberEnd = cal.getTime();
            }

            System.out.println(courseName + ": ");
            java.util.Date currentDate = novemberStart;
            while (novemberEnd.after(currentDate) || novemberEnd.equals(currentDate)) {
                cal.setTime(currentDate);

                if (cal.get(Calendar.DAY_OF_WEEK) == weeklyClassTime) {
                    System.out.println("  " + dateToString(cal) + ": " + militaryTimeToRegularTime(startTime) + " - " + militaryTimeToRegularTime(endTime));
                }

                cal.add(Calendar.DATE, 1);
                Date oldDate = currentDate;
                currentDate = cal.getTime();
                cal.setTime(currentDate);
                if (cal.get(Calendar.MONTH) != Calendar.DECEMBER) {
                    cal.setTime(oldDate);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                    cal.set(Calendar.MONTH, Calendar.DECEMBER);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    currentDate = cal.getTime();
                }
            }
        }

        rs.close();
    }

    /**
     * Query 4: Your additional non-trivial query.
     */
    private static void customQuery(Statement stmt, String userProvidedInfo) throws SQLException {
    }

}

