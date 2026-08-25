package com.attendance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceParser {

    public static List<AttendanceRecord> parse(String jsonResponse) {
        List<AttendanceRecord> records = new ArrayList<>();

        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            return records;
        }

        JsonObject rootObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        if (!rootObject.has("AcsEvent")) {
            return records;
        }

        JsonObject acsEvent = rootObject.getAsJsonObject("AcsEvent");

        if (!acsEvent.has("InfoList")) {
            return records;
        }

        JsonArray infoList = acsEvent.getAsJsonArray("InfoList");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (JsonElement element : infoList) {
            JsonObject eventInfo = element.getAsJsonObject();

            String employeeNo = "";
            if (eventInfo.has("employeeNoString")) {
                employeeNo = eventInfo.get("employeeNoString").getAsString();
            } else if (eventInfo.has("employeeNo")) {
                employeeNo = eventInfo.get("employeeNo").getAsString();
            }

            String datetimeStr = "";
            if (eventInfo.has("time")) {
                datetimeStr = eventInfo.get("time").getAsString();
            }

            if (!employeeNo.isEmpty() && !datetimeStr.isEmpty()) {
                try {
                    OffsetDateTime odt = OffsetDateTime.parse(datetimeStr);
                    String date = odt.format(dateFormatter);
                    String time = odt.format(timeFormatter);
                    records.add(new AttendanceRecord(employeeNo, date, time));
                } catch (Exception e) {
                    records.add(new AttendanceRecord(employeeNo, datetimeStr, datetimeStr));
                }
            }
        }
        return records;
    }
}