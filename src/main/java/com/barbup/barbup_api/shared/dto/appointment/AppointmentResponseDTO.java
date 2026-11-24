package com.barbup.barbup_api.shared.dto.appointment;

import com.barbup.barbup_api.domain.entity.appointment.Appointment;
import com.barbup.barbup_api.domain.entity.appointment.AppointmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AppointmentResponseDTO(
        UUID id,
        UUID barbershopId,
        UUID serviceId,
        String serviceName,
        AvailableBarberDTO barber,
        Instant startAt,
        Instant endAt,
        Integer durationMinutes,
        BigDecimal price,
        AppointmentStatus status,
        String notes
) {
    public AppointmentResponseDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getBarbershop().getId(),
                appointment.getService().getId(),
                appointment.getService().getName(),
                new AvailableBarberDTO(appointment.getBarber().getId(), appointment.getBarber().getUser().getName()),
                appointment.getScheduledAt(),
                appointment.getScheduledAt().plusSeconds(appointment.getDurationMinutes() * 60L),
                appointment.getDurationMinutes(),
                appointment.getPrice(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }
}
