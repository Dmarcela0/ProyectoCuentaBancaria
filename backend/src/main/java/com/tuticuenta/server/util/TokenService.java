package com.tuticuenta.server.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenService {
    private static final String HMAC_ALG = "HmacSHA256";
    private static final String SECRET = "tuticuenta-super-secreto";
    private static final long EXPIRATION_SECONDS = 4 * 60 * 60; // 4 horas

    public String generateToken(String email) {
        long expiration = Instant.now().getEpochSecond() + EXPIRATION_SECONDS;
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":" + JsonUtils.quote(email) + ",\"exp\":" + expiration + "}";
        String encodedHeader = base64Url(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64Url(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedHeader + "." + encodedPayload);
        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public Optional<String> validate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }
        String unsigned = parts[0] + "." + parts[1];
        if (!sign(unsigned).equals(parts[2])) {
            return Optional.empty();
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, String> payload = JsonUtils.parseObject(payloadJson);
        String sub = payload.get("sub");
        String expRaw = payload.get("exp");
        if (sub == null || expRaw == null) {
            return Optional.empty();
        }
        long expiration;
        try {
            expiration = Long.parseLong(expRaw);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
        if (Instant.now().getEpochSecond() > expiration) {
            return Optional.empty();
        }
        return Optional.of(sub);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALG));
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64Url(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible firmar el token", ex);
        }
    }

    private static String base64Url(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
