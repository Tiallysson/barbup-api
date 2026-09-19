package com.barbup.barbup_api.domain.entity.service;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        UUID barbershopId,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes
) {
}
