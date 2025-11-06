package com.tuticuenta.server.account;

import java.time.LocalDate;
import java.util.Optional;

import com.tuticuenta.server.shared.UserAccount;
import com.tuticuenta.server.shared.UserAccountRepository;

public class AccountService {
    private final UserAccountRepository repository;

    public AccountService(UserAccountRepository repository) {
        this.repository = repository;
    }

    public Optional<String> summaryFor(String email) {
        return repository.findByEmail(email).map(UserAccount::summaryJson);
    }

    public Optional<String> recordTransaction(String email, String description, double amount, boolean deposit) {
        if (email == null || email.isBlank() || description == null || description.isBlank() || Double.isNaN(amount) || !Double.isFinite(amount) || amount <= 0) {
            return Optional.empty();
        }
        return repository.findByEmail(email).map(account -> {
            if (deposit) {
                account.deposit(description, amount);
            } else {
                account.withdraw(description, amount);
            }
            repository.save(account);
            return account.summaryJson();
        });
    }

    public Optional<String> createGoal(String email, String name, double targetAmount, LocalDate targetDate) {
        if (email == null || email.isBlank() || name == null || name.isBlank() || targetDate == null || Double.isNaN(targetAmount) || !Double.isFinite(targetAmount) || targetAmount <= 0) {
            return Optional.empty();
        }
        return repository.findByEmail(email).map(account -> {
            account.addGoal(name, targetAmount, targetDate);
            repository.save(account);
            return account.summaryJson();
        });
    }
}
