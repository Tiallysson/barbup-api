package com.barbup.barbup_api.domain.entity.user.dto;

import com.barbup.barbup_api.domain.entity.barbershop.validation.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateRequestDTO(
        @NotBlank
        UUID id,
        @NotBlank
        String name,
        @Email
        @NotBlank
        String email,
        @PhoneNumber
        @NotBlank
        String phone) {
}
