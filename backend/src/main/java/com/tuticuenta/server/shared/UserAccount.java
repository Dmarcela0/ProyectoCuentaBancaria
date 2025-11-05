package com.tuticuenta.server.shared;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tuticuenta.server.util.JsonUtils;

public class UserAccount {
    private final UUID id;
    private final String childName;
    private final int age;
    private final String guardianName;
    private final String guardianEmail;
    private final String passwordHash;
    private double balance;
    private final List<SavingsTransaction> transactions;
    private final List<SavingsGoal> goals;

    public UserAccount(String childName, int age, String guardianName, String guardianEmail, String passwordHash) {
        this.id = UUID.randomUUID();
        this.childName = childName;
        this.age = age;
        this.guardianName = guardianName;
        this.guardianEmail = guardianEmail;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.goals = new ArrayList<>();
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void deposit(String description, double amount) {
        balance += amount;
        transactions.add(new SavingsTransaction(description, amount, LocalDateTime.now()));
    }

    public void withdraw(String description, double amount) {
        balance -= amount;
        transactions.add(new SavingsTransaction(description, -amount, LocalDateTime.now()));
    }

    public void addGoal(String name, double target, LocalDate targetDate) {
        goals.add(new SavingsGoal(name, target, targetDate));
    }

    public String authResponse(String token) {
        return JsonUtils.object(
                JsonUtils.stringField("token", token),
                JsonUtils.stringField("childName", childName),
                JsonUtils.numberField("age", age)
        );
    }

    public String summaryJson() {
        List<String> transactionJson = new ArrayList<>();
        for (SavingsTransaction tx : transactions) {
            transactionJson.add(tx.toJson());
        }
        List<String> goalsJson = new ArrayList<>();
        for (SavingsGoal goal : goals) {
            goalsJson.add(goal.toJson());
        }
        return JsonUtils.object(
                JsonUtils.stringField("childName", childName),
                JsonUtils.numberField("age", age),
                JsonUtils.numberField("balance", balance),
                JsonUtils.quote("transactions") + ":" + JsonUtils.array(transactionJson),
                JsonUtils.quote("goals") + ":" + JsonUtils.array(goalsJson)
        );
    }
}
