package com.barbup.barbup_api.shared.dto.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AvailabilityResponseDTO(
        UUID barbershopId,
        UUID serviceId,
        LocalDate date,
        String timeZone,
        Integer durationMinutes,
        List<AvailableSlotDTO> slots
) {
}
