package com.tuticuenta.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static Map<String, String> parseObject(String json) {
        Map<String, String> result = new HashMap<>();
        if (json == null) {
            return result;
        }
        String trimmed = json.trim();
        if (trimmed.isEmpty()) {
            return result;
        }
        if (trimmed.charAt(0) == '{' && trimmed.charAt(trimmed.length() - 1) == '}') {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        List<String> pairs = splitTopLevel(trimmed, ',');
        for (String pair : pairs) {
            List<String> keyValue = splitTopLevel(pair, ':');
            if (keyValue.size() != 2) {
                continue;
            }
            String key = unquote(keyValue.get(0).trim());
            String value = keyValue.get(1).trim();
            result.put(key, unquote(value));
        }
        return result;
    }

    public static String stringField(String name, String value) {
        return quote(name) + ":" + quote(value == null ? "" : value);
    }

    public static String numberField(String name, Number value) {
        return quote(name) + ":" + (value == null ? "0" : value.toString());
    }

    public static String object(String... entries) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (int i = 0; i < entries.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(entries[i]);
        }
        builder.append('}');
        return builder.toString();
    }

    public static String array(List<String> items) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(items.get(i));
        }
        builder.append(']');
        return builder.toString();
    }

    public static String quote(String value) {
        if (value == null) {
            value = "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '"' || ch == '\\') {
                builder.append('\\');
            }
            builder.append(ch);
        }
        builder.append('"');
        return builder.toString();
    }

    private static List<String> splitTopLevel(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        if (input.isEmpty()) {
            return parts;
        }
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '"') {
                boolean escaped = i > 0 && input.charAt(i - 1) == '\\';
                if (!escaped) {
                    inString = !inString;
                }
            }
            if (ch == delimiter && !inString) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString());
        }
        return parts;
    }

    private static String unquote(String raw) {
        String trimmed = raw.trim();
        if (trimmed.length() >= 2 && trimmed.charAt(0) == '"' && trimmed.charAt(trimmed.length() - 1) == '"') {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            return inner.replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return trimmed;
    }
}
