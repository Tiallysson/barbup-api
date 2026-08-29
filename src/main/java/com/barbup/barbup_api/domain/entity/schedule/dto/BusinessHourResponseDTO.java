package com.barbup.barbup_api.domain.entity.schedule.dto;

import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record BusinessHourResponseDTO(
        UUID id,
        UUID barbershopId,
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime
) {
    public BusinessHourResponseDTO(BusinessHours businessHours) {
        this(
                businessHours.getId(),
                businessHours.getBarbershop().getId(),
                businessHours.getDayOfWeek(),
                businessHours.getOpenTime(),
                businessHours.getCloseTime()
        );
    }
}
