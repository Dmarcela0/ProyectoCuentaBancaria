package com.tuticuenta.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import com.tuticuenta.server.account.AccountService;
import com.tuticuenta.server.auth.AuthService;
import com.tuticuenta.server.database.DatabaseClient;
import com.tuticuenta.server.shared.DemoDataInitializer;
import com.tuticuenta.server.shared.UserAccountRepository;
import com.tuticuenta.server.util.JsonUtils;
import com.tuticuenta.server.util.TokenService;

public class AppServer {

    private final HttpServer httpServer;
    private final AuthService authService;
    private final AccountService accountService;
    private final TokenService tokenService;

    private static final String JDBC_URL = "jdbc:postgresql://ep-shy-cloud-aebwcp72-pooler.c-2.us-east-2.aws.neon.tech:5432/neondb?user=neondb_owner&password=npg_4K9DbkgxeZJl&sslmode=require&channel_binding=require";

    public AppServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        // ✅ Ahora creamos DatabaseClient sin usar resolveDatabaseUrl()
        DatabaseClient databaseClient = new DatabaseClient(JDBC_URL);

        // ✅ Inicializamos DB (crea tablas si no existen)
        databaseClient.initialize();

        UserAccountRepository repository = new UserAccountRepository(databaseClient);
        this.tokenService = new TokenService();
        this.authService = new AuthService(repository, tokenService);
        this.accountService = new AccountService(repository);

        DemoDataInitializer.seed(repository);
        configureRoutes();

        this.httpServer.setExecutor(Executors.newCachedThreadPool());
    }

    private void configureRoutes() {
        httpServer.createContext("/api/auth/register", exchange -> handlePost(exchange, authService::register));
        httpServer.createContext("/api/auth/login", exchange -> handlePost(exchange, authService::login));
        httpServer.createContext("/api/account/summary", this::handleSummary);
        httpServer.createContext("/api/account/transactions", exchange -> handleAuthenticatedPost(exchange, this::handleTransactionCreation));
        httpServer.createContext("/api/account/goals", exchange -> handleAuthenticatedPost(exchange, this::handleGoalCreation));
    }

    private void handlePost(HttpExchange exchange, BodyProcessor processor) throws IOException {
        applyCors(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 204, "");
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Método no permitido");
            return;
        }
        String body = readBody(exchange.getRequestBody());
        Optional<String> response = processor.process(body);
        respond(exchange, response.isPresent() ? 200 : 400, response.orElse("Solicitud inválida"));
    }

    private void handleSummary(HttpExchange exchange) throws IOException {
        applyCors(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 204, "");
            return;
        }
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Método no permitido");
            return;
        }
        Optional<String> email = authenticate(exchange);
        if (email.isEmpty()) {
            return;
        }
        Optional<String> summary = accountService.summaryFor(email.get());
        respond(exchange, summary.isPresent() ? 200 : 404, summary.orElse("Cuenta no encontrada"));
    }

    private void handleAuthenticatedPost(HttpExchange exchange, AuthenticatedBodyProcessor processor) throws IOException {
        applyCors(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 204, "");
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Método no permitido");
            return;
        }
        Optional<String> email = authenticate(exchange);
        if (email.isEmpty()) {
            return;
        }
        String body = readBody(exchange.getRequestBody());
        Optional<String> response = processor.process(email.get(), body);
        respond(exchange, response.isPresent() ? 200 : 400, response.orElse("Solicitud inválida"));
    }

    private Optional<String> handleTransactionCreation(String email, String body) {
        Map<String, String> payload = JsonUtils.parseObject(body);
        String description = payload.getOrDefault("description", "").trim();
        String amountText = payload.getOrDefault("amount", "").trim();
        String type = payload.getOrDefault("type", "deposit").trim();
        if (description.isEmpty() || amountText.isEmpty()) {
            return Optional.empty();
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
        if (amount <= 0) {
            return Optional.empty();
        }
        boolean deposit = !"withdraw".equalsIgnoreCase(type);
        return accountService.recordTransaction(email, description, amount, deposit);
    }

    private Optional<String> handleGoalCreation(String email, String body) {
        Map<String, String> payload = JsonUtils.parseObject(body);
        String name = payload.getOrDefault("name", "").trim();
        String targetAmountText = payload.getOrDefault("targetAmount", "").trim();
        String targetDateText = payload.getOrDefault("targetDate", "").trim();
        if (name.isEmpty() || targetAmountText.isEmpty() || targetDateText.isEmpty()) {
            return Optional.empty();
        }
        double targetAmount;
        try {
            targetAmount = Double.parseDouble(targetAmountText);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
        if (targetAmount <= 0) {
            return Optional.empty();
        }
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(targetDateText);
        } catch (Exception ex) {
            return Optional.empty();
        }
        return accountService.createGoal(email, name, targetAmount, targetDate);
    }

    private Optional<String> authenticate(HttpExchange exchange) throws IOException {
        String token = null;
        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("Authorization")) {
            String value = headers.getFirst("Authorization");
            if (value != null && value.startsWith("Bearer ")) {
                token = value.substring(7);
            }
        }
        Optional<String> email = tokenService.validate(token);
        if (email.isEmpty()) {
            respond(exchange, 401, "Token inválido");
        }
        return email;
    }

    private static void applyCors(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        headers.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.set("Content-Type", "application/json; charset=utf-8");
    }

    private static String readBody(InputStream input) throws IOException {
        byte[] buffer = input.readAllBytes();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, data.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("✅ Servidor Tuticuenta activo en http://localhost:" + httpServer.getAddress().getPort());
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {
            }
        }
        new AppServer(port).start();
    }

    @FunctionalInterface
    private interface BodyProcessor {
        Optional<String> process(String body);
    }

    @FunctionalInterface
    private interface AuthenticatedBodyProcessor {
        Optional<String> process(String email, String body);
    }
}
