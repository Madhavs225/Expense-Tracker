package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseDAO {

    Expense insert(Expense expense);

    boolean update(Expense expense);

    boolean delete(long id);

    Optional<Expense> findById(long id);

    List<Expense> findByDateRange(LocalDate start, LocalDate end);

    List<Expense> findByCategory(int categoryId, LocalDate start, LocalDate end);

    List<Expense> search(String keyword, LocalDate start, LocalDate end);

    List<Expense> listRecent(int limit);

    long count();
    // Additional aggregated queries can be added here
}
