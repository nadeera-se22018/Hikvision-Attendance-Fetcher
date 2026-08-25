package com.attendance;

import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DataFetcher {

    public static String fetchAttendanceJson() throws IOException, ParseException {
        String ip = ConfigManager.get("device.ip");
        String url = "http://" + ip + "/ISAPI/AccessControl/AcsEvent?format=json";

        try (CloseableHttpClient client = HikvisionApiClient.createAuthenticatedClient()) {
            HttpPost httpPost = new HttpPost(url);

            JsonObject acsEventCond = new JsonObject();
            acsEventCond.addProperty("searchID", "1");
            acsEventCond.addProperty("searchResultPosition", 0);
            acsEventCond.addProperty("maxResults", 1000);
            acsEventCond.addProperty("major", 5);
            acsEventCond.addProperty("minor", 75);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            String startTime = ZonedDateTime.now().minusDays(30).format(formatter);
            String endTime = ZonedDateTime.now().format(formatter);

            acsEventCond.addProperty("startTime", startTime);
            acsEventCond.addProperty("endTime", endTime);

            JsonObject payload = new JsonObject();
            payload.add("AcsEventCond", acsEventCond);

            StringEntity entity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    throw new IOException("API request failed with HTTP " + statusCode);
                }
            }
        }
    }
}