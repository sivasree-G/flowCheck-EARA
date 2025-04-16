package org.hbn.flowcheck.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();

    static {
        try {
            // Load the configuration file
            FileInputStream file = new FileInputStream("src/main/resources/config.properties");
            properties.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get the base URI
    public static String getBaseURI() {
        return properties.getProperty("baseURI");
    }

    // Method to get the Excel file path
    public static String getExcelPath() {
        return properties.getProperty("excelPath");
    }

    // Method to get other configuration
    public static String getOtherConfig() {
        return properties.getProperty("otherConfig");
    }
}
