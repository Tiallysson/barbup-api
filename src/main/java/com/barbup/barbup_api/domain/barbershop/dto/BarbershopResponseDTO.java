package com.barbup.barbup_api.domain.barbershop.dto;

import com.barbup.barbup_api.domain.barbershop.Barbershop;

public record BarbershopResponseDTO(
        String id,
        String name,
        String slug,
        String document,
        String phone,
        String address,
        String city,
        String state,
        String logoUrl,
        String userId
) {
    public BarbershopResponseDTO(Barbershop barbershop) {
        this(
                barbershop.getId(),
                barbershop.getName(),
                barbershop.getSlug(),
                barbershop.getDocument(),
                barbershop.getPhone(),
                barbershop.getAddress(),
                barbershop.getCity(),
                barbershop.getState(),
                barbershop.getLogoUrl(),
                barbershop.getOwner().getId()
        );
    }
}
