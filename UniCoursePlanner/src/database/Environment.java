package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Environment {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream input = new FileInputStream(".env")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}