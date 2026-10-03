package com.barbup.barbup_api.shared.dto.auth;

import com.barbup.barbup_api.domain.entity.barbershop.validation.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateRequestDTO(
        @NotBlank
        String name,
        @PhoneNumber
        @NotBlank
        String phone) {
}
