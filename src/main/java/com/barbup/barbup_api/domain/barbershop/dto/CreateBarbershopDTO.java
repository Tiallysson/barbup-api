package com.barbup.barbup_api.domain.barbershop.dto;

import com.barbup.barbup_api.domain.barbershop.Barbershop;
import com.barbup.barbup_api.domain.barbershop.validation.PhoneNumber;
import com.barbup.barbup_api.domain.barbershop.validation.Zipcode;
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
        @NotBlank
        String address,
        @NotBlank
        String city,
        @Size(max = 2)
        @NotBlank
        String state,
        @Zipcode
        @NotBlank
        String zipcode,
        String logoUrl,
        String userId
) {
}
