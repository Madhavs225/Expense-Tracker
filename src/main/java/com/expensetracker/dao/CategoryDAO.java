package com.expensetracker.dao;

import com.expensetracker.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO {

    Category insert(Category category);

    boolean update(Category category);

    boolean delete(int id);

    Optional<Category> findById(int id);

    Optional<Category> findByName(String name);

    List<Category> findAll();

    long count();
}
