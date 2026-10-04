package com.barbup.barbup_api.shared.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record BusinessHourUpdateDTO(
        @NotNull
        UUID id,
        @NotNull
        UUID barbershopId,
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime
) {
}
