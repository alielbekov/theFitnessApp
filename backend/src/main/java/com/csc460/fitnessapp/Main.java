package main.java.com.csc460.fitnessapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            String url = "jdbc:mysql://localhost:3306/yourdatabase";
            String user = "username";
            String password = "password";
            Connection conn = DriverManager.getConnection(url, user, password);

            // Create a statement and execute a query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM yourtable");

            // Process the result set
            while (rs.next()) {
                System.out.println(rs.getString("column_name"));
            }

            // Close the resources
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
