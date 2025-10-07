package com.expensetracker.exception;

/**
 * Wraps lower-level SQL exceptions to decouple from JDBC directly.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
