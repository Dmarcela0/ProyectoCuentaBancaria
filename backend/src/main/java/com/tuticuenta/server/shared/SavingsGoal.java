package com.tuticuenta.server.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.tuticuenta.server.util.JsonUtils;

public class SavingsGoal {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String name;
    private final double targetAmount;
    private final LocalDate targetDate;

    public SavingsGoal(String name, double targetAmount, LocalDate targetDate) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
    }

    public String toJson() {
        return JsonUtils.object(
                JsonUtils.stringField("name", name),
                JsonUtils.numberField("targetAmount", targetAmount),
                JsonUtils.stringField("targetDate", FORMATTER.format(targetDate))
        );
    }
}
