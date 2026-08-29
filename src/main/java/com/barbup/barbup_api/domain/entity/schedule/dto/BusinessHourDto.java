package com.barbup.barbup_api.domain.entity.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record BusinessHourDto(
        @NotBlank
        UUID barbershopId,
        @NotNull
        DayOfWeek dayOfWeek,
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime) {
}
