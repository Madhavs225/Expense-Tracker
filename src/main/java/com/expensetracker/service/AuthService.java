package com.expensetracker.service;

import com.expensetracker.dao.UserAccountDAO;
import com.expensetracker.model.Role;
import com.expensetracker.model.UserAccount;
import com.expensetracker.util.PasswordHasher;
import com.expensetracker.util.SessionContext;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class AuthService {

    private final UserAccountDAO userAccountDAO;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    public UserAccount register(String username, String rawPassword, Role role) {
        String salt = generateSalt();
        String hash = PasswordHasher.hash(rawPassword, salt);
        UserAccount account = new UserAccount(username, hash, salt, role);
        return userAccountDAO.insert(account);
    }

    public boolean login(String username, String rawPassword) {
        Optional<UserAccount> opt = userAccountDAO.findByUsername(username);
        if (opt.isEmpty()) {
            return false;
        }
        UserAccount user = opt.get();
        String expected = user.getPasswordHash();
        String actual = PasswordHasher.hash(rawPassword, user.getSalt());
        if (expected.equals(actual) && user.isActive()) {
            SessionContext.getInstance().setCurrentUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        SessionContext.getInstance().clear();
    }

    private String generateSalt() {
        byte[] b = new byte[24];
        random.nextBytes(b);
        return Base64.getEncoder().encodeToString(b);
    }
}
