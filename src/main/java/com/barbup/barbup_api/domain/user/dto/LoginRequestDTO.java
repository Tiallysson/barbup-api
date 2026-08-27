package com.barbup.barbup_api.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO (
        @NotNull
        String email,
        @NotNull
        String password) {
}
