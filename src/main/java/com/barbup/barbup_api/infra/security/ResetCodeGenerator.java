package com.barbup.barbup_api.infra.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class ResetCodeGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public String hash(String code, UUID userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(userId.toString().getBytes(StandardCharsets.UTF_8));
            byte[] out = digest.digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No such algorithm.", e);
        }
    }

    public String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
