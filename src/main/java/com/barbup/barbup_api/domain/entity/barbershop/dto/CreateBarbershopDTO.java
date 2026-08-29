package com.barbup.barbup_api.domain.entity.barbershop.dto;

import com.barbup.barbup_api.domain.entity.address.dto.AddressDto;
import com.barbup.barbup_api.domain.entity.barbershop.validation.PhoneNumber;
import com.barbup.barbup_api.domain.entity.barbershop.validation.Zipcode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

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
        String userId,
        AddressDto address
) {
}
