package com.barbup.barbup_api.shared.dto.barbershop;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;

import java.util.UUID;

public record BarbershopResponseDTO(
        UUID id,
        String name,
        String slug,
        String document,
        String phone,
        String logoUrl,
        UUID userId,
        Address addressId
) {
    public BarbershopResponseDTO(Barbershop barbershop) {
        this(
                barbershop.getId(),
                barbershop.getName(),
                barbershop.getSlug(),
                barbershop.getDocument(),
                barbershop.getPhone(),
                barbershop.getLogoUrl(),
                barbershop.getOwner().getId(),
                barbershop.getAddress()
        );
    }
}
