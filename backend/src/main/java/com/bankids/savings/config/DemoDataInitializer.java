package com.bankids.savings.config;

import com.bankids.savings.shared.UserAccount;
import com.bankids.savings.shared.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seedDemoAccount() {
        repository.findByEmail("demo@tuticuenta.com").ifPresentOrElse(
                existing -> {},
                () -> {
                    UserAccount demo = new UserAccount(
                            "Alex",
                            10,
                            "Mamá Laura",
                            "demo@tuticuenta.com",
                            passwordEncoder.encode("demo123")
                    );
                    demo.deposit(10, "Tareas del hogar");
                    demo.withdraw(5, "Dulces");
                    repository.save(demo);
                }
        );
    }
}
