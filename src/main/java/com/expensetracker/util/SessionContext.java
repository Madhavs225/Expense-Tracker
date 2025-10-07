package com.expensetracker.util;

import com.expensetracker.model.UserAccount;

/**
 * Holds the currently authenticated user for the running application.
 */
public class SessionContext {

    private static SessionContext INSTANCE;
    private UserAccount currentUser;

    private SessionContext() {
    }

    public static synchronized SessionContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionContext();
        }
        return INSTANCE;
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount user) {
        this.currentUser = user;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void clear() {
        this.currentUser = null;
    }
}
