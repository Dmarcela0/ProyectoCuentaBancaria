package com.tuticuenta.server.shared;

import java.time.LocalDate;

import com.tuticuenta.server.util.PasswordHasher;

public final class DemoDataInitializer {
    private DemoDataInitializer() {
    }

    public static void seed(UserAccountRepository repository) {
        if (!repository.findAll().isEmpty()) {
            return;
        }
        UserAccount demo = new UserAccount(
                "Luna Exploradora",
                10,
                "María Tutora",
                "tutora@tuticuenta.com",
                PasswordHasher.hash("tutisegura")
        );
        demo.deposit("Mesada de abuela", 35.0);
        demo.deposit("Venta de dibujos", 18.5);
        demo.withdraw("Carrito educativo", 12.99);
        demo.addGoal("Microscopio infantil", 120.0, LocalDate.now().plusMonths(6));
        demo.addGoal("Campamento espacial", 220.0, LocalDate.now().plusMonths(12));
        repository.save(demo);
    }
}
