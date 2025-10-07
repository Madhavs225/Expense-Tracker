package com.expensetracker;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.model.Category;
import com.expensetracker.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

/**
 * Simple in-memory stubbed test for CategoryService logic (does not hit DB).
 */
public class CategoryServiceTest {

    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        CategoryDAO stubDao = new CategoryDAO() {
            private int idSeq = 1;
            private final Map<Integer, Category> store = new HashMap<>();

            @Override
            public Category insert(Category category) {
                category.setId(idSeq++);
                store.put(category.getId(), category);
                return category;
            }

            @Override
            public boolean update(Category category) {
                store.put(category.getId(), category);
                return true;
            }

            @Override
            public boolean delete(int id) {
                return store.remove(id) != null;
            }

            @Override
            public Optional<Category> findById(int id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public Optional<Category> findByName(String name) {
                return store.values().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
            }

            @Override
            public List<Category> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public long count() {
                return store.size();
            }
        };
        categoryService = new CategoryService(stubDao);
    }

    @Test
    void testCreateCategory() {
        Category c = categoryService.createCategory("Food", new BigDecimal("5000"));
        Assertions.assertNotNull(c.getId());
        Assertions.assertEquals("Food", c.getName());
    }

    @Test
    void testDuplicateNameThrows() {
        categoryService.createCategory("Travel", null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory("Travel", null));
    }
}
