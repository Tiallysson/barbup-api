package com.barbup.barbup_api.shared.dto.barbershop;

import com.barbup.barbup_api.shared.dto.address.AddressDto;
import com.barbup.barbup_api.domain.entity.barbershop.validation.PhoneNumber;
import com.barbup.barbup_api.domain.entity.barbershop.validation.Zipcode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

public record CreateBarbershopDTO(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        @CPF
        @NotBlank
        String document,
        @PhoneNumber
        @NotBlank
        String phone,
        String logoUrl,
        UUID userId,
        @NotNull(message = "Address cannot possible null")
        @Valid
        AddressDto address
) {
}
