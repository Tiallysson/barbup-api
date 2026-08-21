package com.barbup.barbup_api.domain.barbershop.dto;

import com.barbup.barbup_api.domain.barbershop.Barbershop;

public record CreateBarbershopDTO(
        String name,
        String slug,
        String document,
        String phone,
        String address,
        String city,
        String state,
        String zipcode,
        String logoUrl,
        String userId
) {
}
