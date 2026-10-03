package com.barbup.barbup_api.shared.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationRequestDTO(@NotBlank @Email String email) {
}
