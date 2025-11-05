package com.tuticuenta.server.shared;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserAccountRepository {
    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

    public Optional<UserAccount> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(accounts.get(email.toLowerCase()));
    }

    public void save(UserAccount account) {
        accounts.put(account.getGuardianEmail().toLowerCase(), account);
    }

    public Collection<UserAccount> findAll() {
        return accounts.values();
    }
}
