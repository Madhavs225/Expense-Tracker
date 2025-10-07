package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an expense category with optional monthly budget limit.
 */
public class Category {

    private Integer id; // null until persisted
    private String name;
    private BigDecimal monthlyBudgetLimit; // nullable
    private LocalDateTime createdAt;

    public Category(String name) {
        setName(name);
        this.createdAt = LocalDateTime.now();
    }

    public Category(Integer id, String name, BigDecimal monthlyBudgetLimit, LocalDateTime createdAt) {
        this.id = id;
        this.monthlyBudgetLimit = monthlyBudgetLimit;
        this.createdAt = createdAt;
        setName(name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        this.name = name.trim();
    }

    public BigDecimal getMonthlyBudgetLimit() {
        return monthlyBudgetLimit;
    }

    public void setMonthlyBudgetLimit(BigDecimal monthlyBudgetLimit) {
        if (monthlyBudgetLimit != null && monthlyBudgetLimit.signum() < 0) {
            throw new IllegalArgumentException("Budget limit must be positive");
        }
        this.monthlyBudgetLimit = monthlyBudgetLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(id, category.id) && name.equalsIgnoreCase(category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name.toLowerCase());
    }

    @Override
    public String toString() {
        return name + (monthlyBudgetLimit != null ? " (Limit: " + monthlyBudgetLimit + ")" : "");
    }
}
