package com.barbup.barbup_api.domain.entity.barbershop.dto;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;

public record BarbershopResponseDTO(
        String id,
        String name,
        String slug,
        String document,
        String phone,
        String logoUrl,
        String userId,
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
