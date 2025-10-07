package com.expensetracker.test;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.UserAccountDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.model.UserAccount;
import com.expensetracker.model.Role;
import com.expensetracker.util.PasswordHasher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test data factory for creating consistent test data across all tests
 */
public class TestDataFactory {

    // Test user constants
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_PASSWORD = "testpass123";
    public static final String TEST_ADMIN_USERNAME = "admin";
    public static final String TEST_ADMIN_PASSWORD = "admin123";

    // Test category constants
    public static final String FOOD_CATEGORY = "Food";
    public static final String TRANSPORT_CATEGORY = "Transportation";
    public static final String ENTERTAINMENT_CATEGORY = "Entertainment";
    public static final String UTILITIES_CATEGORY = "Utilities";

    /**
     * Generate a simple salt for testing
     */
    private static String generateTestSalt() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Create a test user account
     */
    public static UserAccount createTestUser() {
        String salt = generateTestSalt();
        String hashedPassword = PasswordHasher.hash(TEST_PASSWORD, salt);

        return new UserAccount(TEST_USERNAME, hashedPassword, salt, Role.USER);
    }

    /**
     * Create a test admin account
     */
    public static UserAccount createTestAdmin() {
        String salt = generateTestSalt();
        String hashedPassword = PasswordHasher.hash(TEST_ADMIN_PASSWORD, salt);

        return new UserAccount(TEST_ADMIN_USERNAME, hashedPassword, salt, Role.ADMIN);
    }

    /**
     * Create a test category with no budget limit
     */
    public static Category createTestCategory(String name) {
        return new Category(name);
    }

    /**
     * Create a test category with budget limit
     */
    public static Category createTestCategoryWithBudget(String name, BigDecimal budgetLimit) {
        Category category = new Category(name);
        category.setMonthlyBudgetLimit(budgetLimit);
        return category;
    }

    /**
     * Create a set of standard test categories
     */
    public static List<Category> createStandardTestCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(createTestCategoryWithBudget(FOOD_CATEGORY, new BigDecimal("500.00")));
        categories.add(createTestCategoryWithBudget(TRANSPORT_CATEGORY, new BigDecimal("200.00")));
        categories.add(createTestCategoryWithBudget(ENTERTAINMENT_CATEGORY, new BigDecimal("150.00")));
        categories.add(createTestCategory(UTILITIES_CATEGORY));
        return categories;
    }

    /**
     * Create a test expense
     */
    public static Expense createTestExpense(Category category, BigDecimal amount, LocalDate date, String description) {
        return Expense.create(category, date, amount, PaymentMethod.CARD, description);
    }

    /**
     * Create a test expense with default values
     */
    public static Expense createTestExpense(Category category, BigDecimal amount) {
        return createTestExpense(category, amount, LocalDate.now(), "Test expense");
    }

    /**
     * Create sample expenses for testing
     */
    public static List<Expense> createSampleExpenses(List<Category> categories) {
        List<Expense> expenses = new ArrayList<>();
        LocalDate today = LocalDate.now();

        Category foodCategory = categories.stream()
                .filter(c -> c.getName().equals(FOOD_CATEGORY))
                .findFirst()
                .orElse(categories.get(0));

        Category transportCategory = categories.stream()
                .filter(c -> c.getName().equals(TRANSPORT_CATEGORY))
                .findFirst()
                .orElse(categories.get(0));

        // Food expenses
        expenses.add(createTestExpense(foodCategory, new BigDecimal("15.99"), today, "Lunch"));
        expenses.add(createTestExpense(foodCategory, new BigDecimal("45.67"), today.minusDays(1), "Groceries"));
        expenses.add(createTestExpense(foodCategory, new BigDecimal("8.50"), today.minusDays(2), "Coffee"));

        // Transport expenses
        expenses.add(createTestExpense(transportCategory, new BigDecimal("25.00"), today, "Gas"));
        expenses.add(createTestExpense(transportCategory, new BigDecimal("3.50"), today.minusDays(1), "Bus fare"));

        return expenses;
    }

    /**
     * Setup test data in the database
     */
    public static TestDataContext setupTestData(UserAccountDAO userDAO, CategoryDAO categoryDAO, ExpenseDAO expenseDAO) throws Exception {
        TestDataContext context = new TestDataContext();

        // Create test users
        context.testUser = createTestUser();
        context.testAdmin = createTestAdmin();

        userDAO.insert(context.testUser);
        userDAO.insert(context.testAdmin);

        // Create test categories
        context.categories = createStandardTestCategories();
        for (Category category : context.categories) {
            categoryDAO.insert(category);
        }

        // Create test expenses
        context.expenses = createSampleExpenses(context.categories);
        for (Expense expense : context.expenses) {
            expenseDAO.insert(expense);
        }

        return context;
    }

    /**
     * Clean up test data from the database
     */
    public static void cleanupTestData(UserAccountDAO userDAO, CategoryDAO categoryDAO, ExpenseDAO expenseDAO, TestDataContext context) {
        try {
            // Clean up in reverse order due to foreign key constraints
            if (context.expenses != null) {
                for (Expense expense : context.expenses) {
                    if (expense.getId() != null) {
                        expenseDAO.delete(expense.getId());
                    }
                }
            }

            if (context.categories != null) {
                for (Category category : context.categories) {
                    if (category.getId() != null) {
                        categoryDAO.delete(category.getId());
                    }
                }
            }

            // Note: User cleanup might not be necessary if using a test database
        } catch (Exception e) {
            // Log but don't fail the test
            System.err.println("Warning: Failed to cleanup test data: " + e.getMessage());
        }
    }

    /**
     * Container for test data context
     */
    public static class TestDataContext {

        public UserAccount testUser;
        public UserAccount testAdmin;
        public List<Category> categories;
        public List<Expense> expenses;
    }
}
