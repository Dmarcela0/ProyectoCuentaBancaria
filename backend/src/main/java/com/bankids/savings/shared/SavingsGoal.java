package com.bankids.savings.shared;

import java.time.LocalDate;

public class SavingsGoal {
    private final String name;
    private final double targetAmount;
    private final LocalDate targetDate;

    public SavingsGoal(String name, double targetAmount, LocalDate targetDate) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }
}
