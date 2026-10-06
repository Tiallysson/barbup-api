package com.barbup.barbup_api.shared.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHourUpdateDTO(
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime
) {
}
