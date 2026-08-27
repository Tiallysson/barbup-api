package com.barbup.barbup_api.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        String email,
        @NotBlank
        String phone,
        @NotBlank
        String password) {
}
