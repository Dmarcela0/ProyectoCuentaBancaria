package com.tuticuenta.server.shared;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tuticuenta.server.util.JsonUtils;

public class SavingsTransaction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String description;
    private final double amount;
    private final LocalDateTime date;

    public SavingsTransaction(String description, double amount, LocalDateTime date) {
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String toJson() {
        return JsonUtils.object(
                JsonUtils.stringField("description", description),
                JsonUtils.numberField("amount", amount),
                JsonUtils.stringField("date", FORMATTER.format(date))
        );
    }
}
