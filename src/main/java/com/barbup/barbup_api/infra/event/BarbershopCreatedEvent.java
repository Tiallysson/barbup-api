package com.barbup.barbup_api.infra.event;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;

public record BarbershopCreatedEvent(String email, String ownerName, String barbershopName, String createdAt) {
}
