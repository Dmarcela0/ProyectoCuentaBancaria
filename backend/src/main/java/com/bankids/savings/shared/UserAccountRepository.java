package com.bankids.savings.shared;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserAccountRepository {

    private final Map<String, UserAccount> accountsByEmail = new ConcurrentHashMap<>();

    public UserAccountRepository() {
    }

    public UserAccount save(UserAccount account) {
        accountsByEmail.put(account.getGuardianEmail(), account);
        return account;
    }

    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(accountsByEmail.get(email));
    }

    public Collection<UserAccount> findAll() {
        return accountsByEmail.values();
    }
}
