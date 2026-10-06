package com.barbup.barbup_api.domain.entity.service;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ServiceRegister(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        @Digits(integer = 36, fraction = 2)
        BigDecimal price,
        @NotNull
        @Positive
        Integer durationMinutes
) {
}
