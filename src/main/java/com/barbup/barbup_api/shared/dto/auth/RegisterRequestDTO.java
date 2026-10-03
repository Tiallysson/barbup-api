package com.barbup.barbup_api.shared.dto.auth;

import com.barbup.barbup_api.domain.entity.barbershop.validation.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank
        String name,
        @Email
        @NotBlank
        String email,
        @PhoneNumber
        @NotBlank
        String phone,
        @NotBlank
        @Size(min = 8, max = 72, message = "The password must be between 8 and 72 characters long.")
        String password) {
}
