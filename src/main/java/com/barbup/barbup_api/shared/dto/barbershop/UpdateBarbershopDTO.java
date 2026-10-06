package com.barbup.barbup_api.shared.dto.barbershop;

import com.barbup.barbup_api.domain.entity.barbershop.validation.PhoneNumber;
import com.barbup.barbup_api.shared.dto.address.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record UpdateBarbershopDTO(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        @PhoneNumber
        @NotBlank
        String phone,
        String logoUrl,
        @NotNull(message = "Address cannot possible null")
        @Valid
        AddressDto address,
        String timeZone
) {
}
