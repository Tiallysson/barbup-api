package com.barbup.barbup_api.shared.dto.address;

import com.barbup.barbup_api.domain.entity.barbershop.validation.Zipcode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record AddressDto(
        @NotBlank
        String street,
        @NotBlank
        String number,
        String complement,
        @NotBlank
        String neighborhood,
        @NotBlank
        String city,
        @Length(min = 2, max = 2)
        @NotBlank
        String state,
        @Zipcode
        @NotBlank
        String zipcode
) {
}
