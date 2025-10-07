package com.expensetracker.service;

import com.expensetracker.model.Category;
import com.expensetracker.test.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Category model and TestDataFactory
 */
public class CategoryServiceTest {

    @Test
    void testCreateTestCategory_ValidName_Success() {
        // Arrange
        String categoryName = "Food";

        // Act
        Category result = TestDataFactory.createTestCategory(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        assertNull(result.getMonthlyBudgetLimit());
    }

    @Test
    void testCreateTestCategoryWithBudget_ValidData_Success() {
        // Arrange
        String categoryName = "Food";
        BigDecimal budgetLimit = new BigDecimal("500.00");

        // Act
        Category result = TestDataFactory.createTestCategoryWithBudget(categoryName, budgetLimit);

        // Assert
        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        assertEquals(budgetLimit, result.getMonthlyBudgetLimit());
    }

    @Test
    void testCreateStandardTestCategories_Success() {
        // Act
        List<Category> result = TestDataFactory.createStandardTestCategories();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());

        // Check that we have the expected categories
        assertTrue(result.stream().anyMatch(c -> c.getName().equals(TestDataFactory.FOOD_CATEGORY)));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals(TestDataFactory.TRANSPORT_CATEGORY)));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals(TestDataFactory.ENTERTAINMENT_CATEGORY)));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals(TestDataFactory.UTILITIES_CATEGORY)));

        // Check that some categories have budget limits
        long categoriesWithBudgets = result.stream()
                .filter(c -> c.getMonthlyBudgetLimit() != null)
                .count();
        assertTrue(categoriesWithBudgets > 0);
    }

    @Test
    void testCategoryBudgetLimits_CorrectValues() {
        // Act
        List<Category> categories = TestDataFactory.createStandardTestCategories();

        // Assert
        Category foodCategory = categories.stream()
                .filter(c -> c.getName().equals(TestDataFactory.FOOD_CATEGORY))
                .findFirst()
                .orElse(null);

        assertNotNull(foodCategory);
        assertEquals(new BigDecimal("500.00"), foodCategory.getMonthlyBudgetLimit());

        Category transportCategory = categories.stream()
                .filter(c -> c.getName().equals(TestDataFactory.TRANSPORT_CATEGORY))
                .findFirst()
                .orElse(null);

        assertNotNull(transportCategory);
        assertEquals(new BigDecimal("200.00"), transportCategory.getMonthlyBudgetLimit());
    }

    @Test
    void testCategoryValidation_NameIsRequired() {
        // Test that a category has a valid name
        Category category = TestDataFactory.createTestCategory("Test Category");

        assertNotNull(category.getName());
        assertFalse(category.getName().trim().isEmpty());
    }

    @Test
    void testCategoryWithZeroBudget_AllowsZeroLimit() {
        // Arrange & Act
        Category category = TestDataFactory.createTestCategoryWithBudget("Test", BigDecimal.ZERO);

        // Assert
        assertNotNull(category);
        assertEquals(BigDecimal.ZERO, category.getMonthlyBudgetLimit());
    }
}
