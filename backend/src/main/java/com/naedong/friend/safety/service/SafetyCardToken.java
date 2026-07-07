package com.naedong.friend.safety.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public final class SafetyCardToken {

    public static final String GENERATED_PUBLIC_TOKEN_PATTERN = "[A-Za-z0-9_-]{43}";
    public static final String LEGACY_UUID_TOKEN_PATTERN =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    public static final String PUBLIC_TOKEN_PATTERN =
            "(?:" + GENERATED_PUBLIC_TOKEN_PATTERN + "|" + LEGACY_UUID_TOKEN_PATTERN + ")";

    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^" + PUBLIC_TOKEN_PATTERN + "$");

    private SafetyCardToken() {
    }

    public static String newToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static void requireValid(String token) {
        if (token == null || !TOKEN_PATTERN.matcher(token).matches()) {
            throw new IllegalArgumentException("Invalid safety card token.");
        }
    }
}
