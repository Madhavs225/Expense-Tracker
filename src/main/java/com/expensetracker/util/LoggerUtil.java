package com.expensetracker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logger access to keep naming consistent.
 */
public final class LoggerUtil {

    private LoggerUtil() {
    }

    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
}
