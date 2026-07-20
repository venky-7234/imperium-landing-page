package com.imperium.ims.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for generating secure random tokens and identifiers.
 */
@UtilityClass
public class TokenUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a URL-safe, 32-character UUID-based token (no dashes).
     *
     * @return a compact UUID token string
     */
    public static String generateUuidToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generates a cryptographically secure Base64-URL encoded token.
     *
     * @param byteLength the number of random bytes (e.g. 32 = 256 bits)
     * @return URL-safe Base64 encoded token
     */
    public static String generateSecureToken(int byteLength) {
        byte[] bytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
