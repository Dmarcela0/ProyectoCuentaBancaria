package com.bankids.savings.shared;

import java.time.LocalDateTime;

public class SavingsTransaction {
    private final String description;
    private final double amount;
    private final LocalDateTime date;

    public SavingsTransaction(String description, double amount, LocalDateTime date) {
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
