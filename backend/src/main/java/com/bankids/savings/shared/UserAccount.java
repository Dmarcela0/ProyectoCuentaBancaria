package com.bankids.savings.shared;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserAccount {
    private final String id;
    private final String childName;
    private final int age;
    private final String guardianName;
    private final String guardianEmail;
    private final String passwordHash;
    private double balance;
    private final List<SavingsTransaction> transactions = new ArrayList<>();
    private final List<SavingsGoal> goals = new ArrayList<>();

    public UserAccount(String childName,
                       int age,
                       String guardianName,
                       String guardianEmail,
                       String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.childName = childName;
        this.age = age;
        this.guardianName = guardianName;
        this.guardianEmail = guardianEmail;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        goals.add(new SavingsGoal("Mi juguete soñado", 100.0, LocalDate.now().plusMonths(6)));
        transactions.add(new SavingsTransaction("Depósito inicial", 20.0, LocalDateTime.now().minusDays(5)));
        transactions.add(new SavingsTransaction("Mesada", 15.0, LocalDateTime.now().minusDays(2)));
        this.balance = transactions.stream().mapToDouble(SavingsTransaction::getAmount).sum();
    }

    public String getId() {
        return id;
    }

    public String getChildName() {
        return childName;
    }

    public int getAge() {
        return age;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public double getBalance() {
        return balance;
    }

    public List<SavingsTransaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public List<SavingsGoal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    public void deposit(double amount, String description) {
        balance += amount;
        transactions.add(new SavingsTransaction(description, amount, LocalDateTime.now()));
    }

    public void withdraw(double amount, String description) {
        balance -= amount;
        transactions.add(new SavingsTransaction(description, -amount, LocalDateTime.now()));
    }
}
