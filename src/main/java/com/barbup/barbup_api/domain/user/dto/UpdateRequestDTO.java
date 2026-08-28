package com.barbup.barbup_api.domain.user.dto;

import com.barbup.barbup_api.domain.barbershop.validation.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateRequestDTO(
        @NotBlank
        String id,
        @NotBlank
        String name,
        @Email
        @NotBlank
        String email,
        @PhoneNumber
        @NotBlank
        String phone) {
}
