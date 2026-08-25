package com.attendance;

public class AttendanceRecord {
    private String userId;
    private String date;
    private String time;

    public AttendanceRecord(String userId, String date, String time) {
        this.userId = userId;
        this.date = date;
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}