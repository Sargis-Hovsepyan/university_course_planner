package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Environment {
    private static final Properties props = new Properties();

    static {

        try {
            // Go two levels up from src folder (project root + one more)
            String envPath = System.getProperty("user.dir") + "/.env";
            FileInputStream fis = new FileInputStream(envPath);
            props.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("Failed to load .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
