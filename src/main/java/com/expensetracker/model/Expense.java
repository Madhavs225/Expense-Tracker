package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single expense record.
 */
public class Expense {

    private Long id; // null until persisted
    private Category category;
    private LocalDate date;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String description; // optional
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Expense() {
    }

    public static Expense create(Category category, LocalDate date, BigDecimal amount, PaymentMethod paymentMethod, String description) {
        Expense e = new Expense();
        e.setCategory(category);
        e.setDate(date);
        e.setAmount(amount);
        e.setPaymentMethod(paymentMethod);
        e.setDescription(description);
        e.createdAt = LocalDateTime.now();
        return e;
    }

    public void markPersisted(Long id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category required");
        }
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date required");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Expense date cannot be in the future");
        }
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            paymentMethod = PaymentMethod.OTHER;
        }
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("Description too long (max 255)");
        }
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expense)) {
            return false;
        }
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return date + " - " + category.getName() + ": " + amount;
    }
}
