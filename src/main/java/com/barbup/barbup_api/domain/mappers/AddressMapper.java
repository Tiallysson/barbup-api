package com.barbup.barbup_api.domain.mappers;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.shared.dto.address.AddressDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDto dto);
}