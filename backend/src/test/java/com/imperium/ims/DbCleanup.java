package com.imperium.ims;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class DbCleanup {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/imperium_dev?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "venky2319@";

        List<String> tablesToClear = Arrays.asList(
            "applications",
            "invitations",
            "notifications",
            "audit_logs",
            "email_logs",
            "whatsapp_logs",
            "email_queue",
            "whatsapp_queue",
            "event_scans"
        );

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // Disable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");

            List<String> report = new ArrayList<>();
            report.add("Database Cleanup Report");
            report.add("=========================");

            for (String table : tablesToClear) {
                // Check if table exists
                ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + table + "'");
                if (rs.next()) {
                    // Count rows before
                    ResultSet rsCount = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                    int countBefore = 0;
                    if (rsCount.next()) {
                        countBefore = rsCount.getInt(1);
                    }
                    
                    if (countBefore > 0) {
                        stmt.executeUpdate("DELETE FROM " + table);
                        report.add("- Removed " + countBefore + " records from " + table);
                    } else {
                        report.add("- No records to remove in " + table);
                    }
                }
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            
            System.out.println("CLEANUP_SUCCESS");
            for (String line : report) {
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
