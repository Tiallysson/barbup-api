package com.barbup.barbup_api.shared.dto.appointment;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

public record AvailableSlotDTO(
        LocalTime time,
        Instant startAt,
        Instant endAt,
        List<AvailableBarberDTO> barbers
) {
}
