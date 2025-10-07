package com.expensetracker.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple singleton-style DB connection provider (not a real pool but
 * centralizes config).
 */
public class DBConnectionManager {

    private static DBConnectionManager INSTANCE;
    private String url;
    private String user;
    private String password;

    private DBConnectionManager() {
        loadConfig();
    }

    public static synchronized DBConnectionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBConnectionManager();
        }
        return INSTANCE;
    }

    private void loadConfig() {
        Properties props = new Properties();
        // 1. Load base config.properties from classpath
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load base DB config", e);
        }

        // 2. Optional override: config.local.properties (classpath first)
        try (InputStream localIn = getClass().getClassLoader().getResourceAsStream("config.local.properties")) {
            if (localIn != null) {
                Properties localProps = new Properties();
                localProps.load(localIn);
                props.putAll(localProps);
            }
        } catch (IOException e) {
            // Ignore but log
            System.err.println("[WARN] Failed reading classpath config.local.properties: " + e.getMessage());
        }

        // 3. Optional override from working directory (not packaged) if file exists
        Path wdOverride = Paths.get(System.getProperty("user.dir"), "config.local.properties");
        if (Files.isRegularFile(wdOverride)) {
            try (InputStream fis = Files.newInputStream(wdOverride)) {
                Properties localProps = new Properties();
                localProps.load(fis);
                props.putAll(localProps);
            } catch (IOException e) {
                System.err.println("[WARN] Failed reading working directory config.local.properties: " + e.getMessage());
            }
        }

        // 4. Environment variable overrides (highest precedence)
        overrideIfEnvPresent(props, "db.url", "DB_URL");
        overrideIfEnvPresent(props, "db.user", "DB_USER");
        overrideIfEnvPresent(props, "db.password", "DB_PASSWORD");

        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException("Database configuration incomplete (url/user/password)");
        }
    }

    private void overrideIfEnvPresent(Properties props, String key, String envName) {
        String val = System.getenv(envName);
        if (val != null && !val.isBlank()) {
            props.setProperty(key, val);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
