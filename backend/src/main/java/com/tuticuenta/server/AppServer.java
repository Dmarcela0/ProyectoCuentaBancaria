package com.tuticuenta.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import com.tuticuenta.server.account.AccountService;
import com.tuticuenta.server.auth.AuthService;
import com.tuticuenta.server.shared.DemoDataInitializer;
import com.tuticuenta.server.shared.UserAccountRepository;
import com.tuticuenta.server.util.TokenService;

public class AppServer {
    private final HttpServer httpServer;
    private final AuthService authService;
    private final AccountService accountService;
    private final TokenService tokenService;

    public AppServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        UserAccountRepository repository = new UserAccountRepository();
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
        if (response.isPresent()) {
            respond(exchange, 200, response.get());
        } else {
            respond(exchange, 400, "Solicitud inválida");
        }
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
            return;
        }
        Optional<String> summary = accountService.summaryFor(email.get());
        if (summary.isEmpty()) {
            respond(exchange, 404, "Cuenta no encontrada");
            return;
        }
        respond(exchange, 200, summary.get());
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
        System.out.println("Servidor Tuticuenta activo en http://localhost:" + httpServer.getAddress().getPort());
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
}
