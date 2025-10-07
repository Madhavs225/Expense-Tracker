package com.expensetracker.dao;

import com.expensetracker.model.UserAccount;
import java.util.Optional;

public interface UserAccountDAO {

    UserAccount insert(UserAccount user);

    Optional<UserAccount> findByUsername(String username);
}
