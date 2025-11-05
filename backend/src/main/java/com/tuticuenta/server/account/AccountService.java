package com.tuticuenta.server.account;

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
}
