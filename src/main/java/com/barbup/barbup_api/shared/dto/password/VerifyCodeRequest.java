package com.barbup.barbup_api.shared.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyCodeRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Pattern(regexp = "\\d{6}", message = "O código deve ter 6 dígitos")
        String code) {
}
