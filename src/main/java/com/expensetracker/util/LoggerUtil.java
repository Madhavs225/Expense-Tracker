package com.expensetracker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced logging utility with support for all logging levels and convenience
 * methods
 */
public final class LoggerUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    private LoggerUtil() {
    }

    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }

    // Convenience methods for common logging operations
    public static void trace(String message) {
        logger.trace(message);
    }

    public static void trace(String message, Throwable throwable) {
        logger.trace(message, throwable);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    // Legacy methods for backward compatibility
    public static void log(String message) {
        info(message);
    }

    public static void logError(String message, Throwable throwable) {
        error(message, throwable);
    }
}
