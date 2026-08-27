package com.barbup.barbup_api.domain.barbershop.dto;

import com.barbup.barbup_api.domain.barbershop.Barbershop;
import jakarta.validation.constraints.NotBlank;

public record CreateBarbershopDTO(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        @NotBlank
        String document,
        String phone,
        @NotBlank
        String address,
        @NotBlank
        String city,
        @NotBlank
        String state,
        @NotBlank
        String zipcode,
        String logoUrl,
        String userId
) {
}
