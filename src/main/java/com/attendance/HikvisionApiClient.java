package com.attendance;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;

public class HikvisionApiClient {

    public static CloseableHttpClient createAuthenticatedClient() {
        String ip = ConfigManager.get("device.ip");
        String username = ConfigManager.get("device.username");
        String password = ConfigManager.get("device.password");

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(
                new AuthScope(new HttpHost("http", ip, 80)),
                new UsernamePasswordCredentials(username, password.toCharArray())
        );

        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}
