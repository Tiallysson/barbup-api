package com.barbup.barbup_api.domain.mappers;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.domain.entity.address.dto.AddressDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDto dto);
}