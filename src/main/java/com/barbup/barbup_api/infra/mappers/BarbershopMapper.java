package com.barbup.barbup_api.infra.mappers;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.shared.dto.barbershop.BarbershopResponseDTO;
import com.barbup.barbup_api.shared.dto.barbershop.CreateBarbershopDTO;
import com.barbup.barbup_api.domain.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BarbershopMapper {
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.phone", target = "phone")
    @Mapping(source = "dto.slug", target = "slug")
    @Mapping(source = "dto.document", target = "document")
    @Mapping(source = "dto.logoUrl", target = "logoUrl")
    @Mapping(source = "dto.address", target = "address")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "owner", source = "owner")
    Barbershop toEntity(CreateBarbershopDTO dto, User owner);

    @Mapping(target = "userId", source = "owner.id")
    BarbershopResponseDTO toBarbershopResponseDTO(Barbershop barbershop);

    List<BarbershopResponseDTO> toBarbershopResponseDTOList(List<Barbershop> barbershops);
}
