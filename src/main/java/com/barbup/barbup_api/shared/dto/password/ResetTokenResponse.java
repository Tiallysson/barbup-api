package com.barbup.barbup_api.shared.dto.password;

public record ResetTokenResponse(String token, long expiresInSeconds) {
}
