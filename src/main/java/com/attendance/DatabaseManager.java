package com.attendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:attendance.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS attendance_records (" +
                "user_id TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "UNIQUE(user_id, date, time)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static void saveRecords(List<AttendanceRecord> records) {
        String insertSQL = "INSERT OR IGNORE INTO attendance_records (user_id, date, time) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false);

            for (AttendanceRecord record : records) {
                pstmt.setString(1, record.getUserId());
                pstmt.setString(2, record.getDate());
                pstmt.setString(3, record.getTime());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save records to database", e);
        }
    }

    public static List<AttendanceRecord> getAllRecords() {
        List<AttendanceRecord> records = new ArrayList<>();
        String querySQL = "SELECT user_id, date, time FROM attendance_records ORDER BY date DESC, time DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String date = rs.getString("date");
                String time = rs.getString("time");
                records.add(new AttendanceRecord(userId, date, time));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to read records from database", e);
        }
        return records;
    }
}