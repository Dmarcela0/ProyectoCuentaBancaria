package com.tuticuenta.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseClient {
    private final String jdbcUrl;

    // ✅ Constructor correcto: usa el parámetro recibido
    public DatabaseClient(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public void initialize() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("No se encontró el controlador JDBC de PostgreSQL en el classpath.");
        }

        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS tuticuenta_accounts (" +
                    "id UUID PRIMARY KEY," +
                    "child_name TEXT NOT NULL," +
                    "age INTEGER NOT NULL," +
                    "guardian_name TEXT NOT NULL," +
                    "guardian_email TEXT UNIQUE NOT NULL," +
                    "password_hash TEXT NOT NULL," +
                    "balance NUMERIC NOT NULL DEFAULT 0" +
                    ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS tuticuenta_transactions (" +
                    "id UUID PRIMARY KEY," +
                    "account_id UUID REFERENCES tuticuenta_accounts(id) ON DELETE CASCADE," +
                    "description TEXT NOT NULL," +
                    "amount NUMERIC NOT NULL," +
                    "created_at TIMESTAMP NOT NULL" +
                    ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS tuticuenta_goals (" +
                    "id UUID PRIMARY KEY," +
                    "account_id UUID REFERENCES tuticuenta_accounts(id) ON DELETE CASCADE," +
                    "name TEXT NOT NULL," +
                    "target_amount NUMERIC NOT NULL," +
                    "target_date DATE NOT NULL" +
                    ")");
        } catch (SQLException ex) {
            throw new IllegalStateException("No fue posible preparar las tablas de Tuticuenta", ex);
        }
    }
}
