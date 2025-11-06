package com.tuticuenta.server.shared;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import com.tuticuenta.server.database.DatabaseClient;

public class UserAccountRepository {
    private final DatabaseClient databaseClient;

    public UserAccountRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Optional<UserAccount> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        String query = "SELECT id, child_name, age, guardian_name, guardian_email, password_hash, balance " +
                "FROM tuticuenta_accounts WHERE LOWER(guardian_email) = ?";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email.toLowerCase(Locale.ROOT));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                UUID id = UUID.fromString(resultSet.getString("id"));
                String childName = resultSet.getString("child_name");
                int age = resultSet.getInt("age");
                String guardianName = resultSet.getString("guardian_name");
                String guardianEmail = resultSet.getString("guardian_email");
                String passwordHash = resultSet.getString("password_hash");
                BigDecimal balanceValue = resultSet.getBigDecimal("balance");
                double balance = balanceValue != null ? balanceValue.doubleValue() : 0.0;
                List<SavingsTransaction> transactions = loadTransactions(connection, id);
                List<SavingsGoal> goals = loadGoals(connection, id);
                return Optional.of(new UserAccount(
                        id,
                        childName,
                        age,
                        guardianName,
                        guardianEmail,
                        passwordHash,
                        balance,
                        transactions,
                        goals
                ));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No fue posible consultar la cuenta", ex);
        }
    }

    public void save(UserAccount account) {
        try (Connection connection = databaseClient.getConnection()) {
            connection.setAutoCommit(false);
            try {
                UUID persistedId = upsertAccount(connection, account);
                replaceTransactions(connection, persistedId, account.getTransactions());
                replaceGoals(connection, persistedId, account.getGoals());
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No fue posible guardar la cuenta", ex);
        }
    }

    public boolean hasAnyAccount() {
        try (Connection connection = databaseClient.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1 FROM tuticuenta_accounts LIMIT 1")) {
            return resultSet.next();
        } catch (SQLException ex) {
            throw new IllegalStateException("No fue posible verificar si existen cuentas", ex);
        }
    }

    private UUID upsertAccount(Connection connection, UserAccount account) throws SQLException {
        String statementSql = "INSERT INTO tuticuenta_accounts (id, child_name, age, guardian_name, guardian_email, password_hash, balance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (guardian_email) DO UPDATE SET " +
                "child_name = EXCLUDED.child_name, " +
                "age = EXCLUDED.age, " +
                "guardian_name = EXCLUDED.guardian_name, " +
                "password_hash = EXCLUDED.password_hash, " +
                "balance = EXCLUDED.balance " +
                "RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(statementSql)) {
            statement.setObject(1, account.getId());
            statement.setString(2, account.getChildName());
            statement.setInt(3, account.getAge());
            statement.setString(4, account.getGuardianName());
            statement.setString(5, account.getGuardianEmail());
            statement.setString(6, account.getPasswordHash());
            statement.setBigDecimal(7, BigDecimal.valueOf(account.getBalance()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return UUID.fromString(resultSet.getString(1));
                }
                return account.getId();
            }
        }
    }

    private void replaceTransactions(Connection connection, UUID accountId, List<SavingsTransaction> transactions) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM tuticuenta_transactions WHERE account_id = ?")) {
            deleteStatement.setObject(1, accountId);
            deleteStatement.executeUpdate();
        }
        if (transactions.isEmpty()) {
            return;
        }
        String insertSql = "INSERT INTO tuticuenta_transactions (id, account_id, description, amount, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            for (SavingsTransaction transaction : transactions) {
                insertStatement.setObject(1, UUID.randomUUID());
                insertStatement.setObject(2, accountId);
                insertStatement.setString(3, transaction.getDescription());
                insertStatement.setBigDecimal(4, BigDecimal.valueOf(transaction.getAmount()));
                insertStatement.setTimestamp(5, Timestamp.valueOf(transaction.getDate()));
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    private void replaceGoals(Connection connection, UUID accountId, List<SavingsGoal> goals) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM tuticuenta_goals WHERE account_id = ?")) {
            deleteStatement.setObject(1, accountId);
            deleteStatement.executeUpdate();
        }
        if (goals.isEmpty()) {
            return;
        }
        String insertSql = "INSERT INTO tuticuenta_goals (id, account_id, name, target_amount, target_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            for (SavingsGoal goal : goals) {
                insertStatement.setObject(1, UUID.randomUUID());
                insertStatement.setObject(2, accountId);
                insertStatement.setString(3, goal.getName());
                insertStatement.setBigDecimal(4, BigDecimal.valueOf(goal.getTargetAmount()));
                insertStatement.setDate(5, Date.valueOf(goal.getTargetDate()));
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    private List<SavingsTransaction> loadTransactions(Connection connection, UUID accountId) throws SQLException {
        String sql = "SELECT description, amount, created_at FROM tuticuenta_transactions WHERE account_id = ? ORDER BY created_at DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<SavingsTransaction> transactions = new ArrayList<>();
                while (resultSet.next()) {
                    String description = resultSet.getString("description");
                    BigDecimal amountValue = resultSet.getBigDecimal("amount");
                    double amount = amountValue != null ? amountValue.doubleValue() : 0.0;
                    Timestamp timestamp = resultSet.getTimestamp("created_at");
                    LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
                    transactions.add(new SavingsTransaction(description, amount, date));
                }
                return transactions;
            }
        }
    }

    private List<SavingsGoal> loadGoals(Connection connection, UUID accountId) throws SQLException {
        String sql = "SELECT name, target_amount, target_date FROM tuticuenta_goals WHERE account_id = ? ORDER BY target_date";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<SavingsGoal> goals = new ArrayList<>();
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    BigDecimal targetAmountValue = resultSet.getBigDecimal("target_amount");
                    double targetAmount = targetAmountValue != null ? targetAmountValue.doubleValue() : 0.0;
                    Date targetDateValue = resultSet.getDate("target_date");
                    LocalDate targetDate = targetDateValue != null ? targetDateValue.toLocalDate() : LocalDate.now();
                    goals.add(new SavingsGoal(name, targetAmount, targetDate));
                }
                return goals;
            }
        }
    }
}
