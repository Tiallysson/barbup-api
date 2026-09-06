package com.barbup.barbup_api.shared.dto.password;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank
        String hashToken,
        @NotBlank
        String newPassword) {
}
