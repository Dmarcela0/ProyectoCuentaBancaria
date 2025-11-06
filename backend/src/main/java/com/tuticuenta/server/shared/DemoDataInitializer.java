package com.tuticuenta.server.shared;

import java.time.LocalDate;

import com.tuticuenta.server.util.PasswordHasher;

public final class DemoDataInitializer {
    private DemoDataInitializer() {
    }

    public static void seed(UserAccountRepository repository) {
        if (repository.hasAnyAccount()) {
            return;
        }
        UserAccount demo = new UserAccount(
                "Luna Exploradora",
                10,
                "María Tutora",
                "tutora@tuticuenta.com",
                PasswordHasher.hash("tutisegura")
        );
        demo.deposit("Mesada de la abuela", 35000);
        demo.deposit("Venta de manillas en la escuela", 18500);
        demo.withdraw("Libro de ciencia ficción", 12990);
        demo.addGoal("Microscopio infantil", 320000, LocalDate.now().plusMonths(6));
        demo.addGoal("Campamento astronómico en Villa de Leyva", 780000, LocalDate.now().plusMonths(12));
        repository.save(demo);
    }
}
