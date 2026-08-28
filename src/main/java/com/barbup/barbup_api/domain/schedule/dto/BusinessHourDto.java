package com.barbup.barbup_api.domain.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHourDto(
        @NotBlank
        String barbershopId,
        @NotNull
        DayOfWeek dayOfWeek,
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime) {
}
