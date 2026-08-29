package com.barbup.barbup_api.domain.mappers;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.barbershop.dto.CreateBarbershopDTO;
import com.barbup.barbup_api.domain.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BarbershopMapper {
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.phone", target = "phone")
    Barbershop toEntity(CreateBarbershopDTO dto, User owner);
}
