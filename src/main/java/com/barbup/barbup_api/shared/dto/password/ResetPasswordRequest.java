package com.barbup.barbup_api.shared.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank
        String rawToken,
        @Email
        @NotBlank
        String email,
        @NotBlank
        String newPassword) {
}
