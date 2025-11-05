package com.tuticuenta.server.auth;

import java.util.Map;
import java.util.Optional;

import com.tuticuenta.server.shared.UserAccount;
import com.tuticuenta.server.shared.UserAccountRepository;
import com.tuticuenta.server.util.JsonUtils;
import com.tuticuenta.server.util.PasswordHasher;
import com.tuticuenta.server.util.TokenService;

public class AuthService {
    private final UserAccountRepository repository;
    private final TokenService tokenService;

    public AuthService(UserAccountRepository repository, TokenService tokenService) {
        this.repository = repository;
        this.tokenService = tokenService;
    }

    public Optional<String> register(String body) {
        Map<String, String> payload = JsonUtils.parseObject(body);
        String childName = payload.get("childName");
        String ageRaw = payload.get("age");
        String guardianName = payload.get("guardianName");
        String guardianEmail = payload.get("guardianEmail");
        String password = payload.get("password");

        if (childName == null || guardianName == null || guardianEmail == null || password == null || ageRaw == null) {
            return Optional.empty();
        }
        int age;
        try {
            age = Integer.parseInt(ageRaw);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
        if (age < 7 || age > 14) {
            return Optional.empty();
        }
        if (repository.findByEmail(guardianEmail).isPresent()) {
            return Optional.empty();
        }
        UserAccount account = new UserAccount(childName, age, guardianName, guardianEmail, PasswordHasher.hash(password));
        repository.save(account);
        String token = tokenService.generateToken(guardianEmail);
        return Optional.of(account.authResponse(token));
    }

    public Optional<String> login(String body) {
        Map<String, String> payload = JsonUtils.parseObject(body);
        String email = payload.get("email");
        String password = payload.get("password");
        if (email == null || password == null) {
            return Optional.empty();
        }
        Optional<UserAccount> account = repository.findByEmail(email);
        if (account.isEmpty()) {
            return Optional.empty();
        }
        if (!PasswordHasher.matches(password, account.get().getPasswordHash())) {
            return Optional.empty();
        }
        String token = tokenService.generateToken(email);
        return Optional.of(account.get().authResponse(token));
    }
}
