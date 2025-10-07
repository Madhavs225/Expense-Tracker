package com.expensetracker.service;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.model.Category;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for category operations with validation and caching
 * placeholder.
 */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public Category createCategory(String name, BigDecimal monthlyLimit) {
        if (categoryDAO.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Category name already exists");
        }
        Category category = new Category(name);
        category.setMonthlyBudgetLimit(monthlyLimit);
        return categoryDAO.insert(category);
    }

    public boolean updateCategory(Category category) {
        return categoryDAO.update(category);
    }

    public boolean deleteCategory(int id) {
        // TODO: Add check for existing expenses referencing the category
        return categoryDAO.delete(id);
    }

    public List<Category> listCategories() {
        return categoryDAO.findAll();
    }

    public Optional<Category> findByName(String name) {
        return categoryDAO.findByName(name);
    }
}
