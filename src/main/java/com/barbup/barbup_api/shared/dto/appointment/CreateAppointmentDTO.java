package com.barbup.barbup_api.shared.dto.appointment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateAppointmentDTO(
        @NotNull
        UUID serviceId,
        @NotNull
        Instant startAt,
        UUID barberId,
        @Size(max = 255)
        String notes
) {
}
