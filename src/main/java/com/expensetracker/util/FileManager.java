package com.expensetracker.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Centralized file management for the expense tracker application. Handles
 * creation of directories, file operations, and path management.
 */
public class FileManager {

    private static FileManager INSTANCE;
    private static final ReentrantLock lock = new ReentrantLock();

    // Application directories
    private final Path appDataDir;
    private final Path reportsDir;
    private final Path logsDir;
    private final Path backupDir;
    private final Path tempDir;

    private static final String APP_DIR_NAME = "ExpenseTracker";

    private FileManager() {
        // Initialize application directories
        String userHome = System.getProperty("user.home");
        this.appDataDir = Paths.get(userHome, APP_DIR_NAME);
        this.reportsDir = appDataDir.resolve("reports");
        this.logsDir = appDataDir.resolve("logs");
        this.backupDir = appDataDir.resolve("backups");
        this.tempDir = appDataDir.resolve("temp");

        initializeDirectories();
    }

    public static FileManager getInstance() {
        if (INSTANCE == null) {
            lock.lock();
            try {
                if (INSTANCE == null) {
                    INSTANCE = new FileManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return INSTANCE;
    }

    /**
     * Creates all necessary application directories
     */
    private void initializeDirectories() {
        try {
            Files.createDirectories(appDataDir);
            Files.createDirectories(reportsDir);
            Files.createDirectories(logsDir);
            Files.createDirectories(backupDir);
            Files.createDirectories(tempDir);

            LoggerUtil.info("Application directories initialized: " + appDataDir);
        } catch (IOException e) {
            LoggerUtil.error("Failed to create application directories", e);
            throw new RuntimeException("Could not initialize application directories", e);
        }
    }

    /**
     * Gets the reports directory path
     */
    public Path getReportsDir() {
        return reportsDir;
    }

    /**
     * Gets the logs directory path
     */
    public Path getLogsDir() {
        return logsDir;
    }

    /**
     * Gets the backup directory path
     */
    public Path getBackupDir() {
        return backupDir;
    }

    /**
     * Gets the temporary files directory path
     */
    public Path getTempDir() {
        return tempDir;
    }

    /**
     * Creates a unique filename with timestamp
     */
    public String createTimestampedFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }

    /**
     * Creates a report file path with the given name
     */
    public Path createReportPath(String filename) {
        return reportsDir.resolve(filename);
    }

    /**
     * Creates a log file path with the given name
     */
    public Path createLogPath(String filename) {
        return logsDir.resolve(filename);
    }

    /**
     * Creates a backup file path with the given name
     */
    public Path createBackupPath(String filename) {
        return backupDir.resolve(filename);
    }

    /**
     * Safely writes content to a file
     */
    public void writeToFile(Path filePath, String content) throws IOException {
        // Ensure parent directory exists
        Files.createDirectories(filePath.getParent());

        // Write content
        Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        LoggerUtil.debug("File written successfully: " + filePath);
    }

    /**
     * Safely appends content to a file
     */
    public void appendToFile(Path filePath, String content) throws IOException {
        // Ensure parent directory exists
        Files.createDirectories(filePath.getParent());

        // Append content
        Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        LoggerUtil.debug("Content appended to file: " + filePath);
    }

    /**
     * Checks if a file exists and is readable
     */
    public boolean isFileReadable(Path filePath) {
        return Files.exists(filePath) && Files.isReadable(filePath);
    }

    /**
     * Cleans up old files in a directory (older than specified days)
     */
    public void cleanupOldFiles(Path directory, int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);

            Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(file -> {
                        try {
                            return Files.getLastModifiedTime(file).toInstant()
                                    .isBefore(cutoffDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                        } catch (IOException e) {
                            LoggerUtil.warn("Could not check file modification time: " + file);
                            return false;
                        }
                    })
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            LoggerUtil.debug("Deleted old file: " + file);
                        } catch (IOException e) {
                            LoggerUtil.warn("Could not delete old file: " + file, e);
                        }
                    });

        } catch (IOException e) {
            LoggerUtil.warn("Could not cleanup old files in directory: " + directory, e);
        }
    }

    /**
     * Gets the size of a directory in bytes
     */
    public long getDirectorySize(Path directory) {
        try {
            return Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .mapToLong(file -> {
                        try {
                            return Files.size(file);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            LoggerUtil.warn("Could not calculate directory size: " + directory, e);
            return 0L;
        }
    }
}
