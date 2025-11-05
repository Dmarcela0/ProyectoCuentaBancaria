package com.bankids.savings.account;

import com.bankids.savings.shared.SavingsGoal;
import com.bankids.savings.shared.SavingsTransaction;

import java.util.List;

public class AccountSummary {
    private final String childName;
    private final int age;
    private final double balance;
    private final List<SavingsTransaction> transactions;
    private final List<SavingsGoal> goals;

    public AccountSummary(String childName, int age, double balance,
                          List<SavingsTransaction> transactions,
                          List<SavingsGoal> goals) {
        this.childName = childName;
        this.age = age;
        this.balance = balance;
        this.transactions = transactions;
        this.goals = goals;
    }

    public String getChildName() {
        return childName;
    }

    public int getAge() {
        return age;
    }

    public double getBalance() {
        return balance;
    }

    public List<SavingsTransaction> getTransactions() {
        return transactions;
    }

    public List<SavingsGoal> getGoals() {
        return goals;
    }
}
