package com.barbup.barbup_api.shared.dto.schedule;

import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHourDto(
        @NotNull
        DayOfWeek dayOfWeek,
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime) {
}
