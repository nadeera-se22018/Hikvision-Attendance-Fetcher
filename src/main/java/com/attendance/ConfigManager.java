package com.attendance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();

    static {
        try {
            File externalConfig = new File("config.properties");

            if (externalConfig.exists()) {
                try (InputStream input = new FileInputStream(externalConfig)) {
                    properties.load(input);
                }
            } else {
                try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
                    if (input != null) {
                        properties.load(input);
                    } else {
                        throw new RuntimeException("config.properties not found");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}