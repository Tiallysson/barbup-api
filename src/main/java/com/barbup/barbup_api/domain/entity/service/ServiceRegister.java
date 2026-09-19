package com.barbup.barbup_api.domain.entity.service;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceRegister(
        @NotNull
        UUID barbershopId,
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        BigDecimal price,
        @NotNull
        Integer durationMinutes
) {
}
