package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.infra.event.BarbershopCreatedEvent;
import com.barbup.barbup_api.shared.dto.barbershop.BarbershopResponseDTO;
import com.barbup.barbup_api.shared.dto.barbershop.CreateBarbershopDTO;
import com.barbup.barbup_api.shared.dto.barbershop.UpdateBarbershopDTO;
import com.barbup.barbup_api.shared.exception.InvalidTimeZoneException;
import org.springframework.security.access.prepost.PreAuthorize;
import com.barbup.barbup_api.domain.entity.barbershop.validation.Zipcode;
import com.barbup.barbup_api.domain.entity.member.Member;
import com.barbup.barbup_api.domain.entity.member.MemberRole;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.infra.mappers.AddressMapper;
import com.barbup.barbup_api.infra.mappers.BarbershopMapper;
import com.barbup.barbup_api.infra.persistence.BarbershopRepository;
import com.barbup.barbup_api.infra.persistence.MemberRepository;
import com.barbup.barbup_api.infra.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarbershopService {
    private final UserRepository userRepository;
    private final BarbershopRepository barbershopRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final AddressMapper addressMapper;
    private final BarbershopMapper barbershopMapper;

    public Barbershop createBarbershop(CreateBarbershopDTO dto, User authenticatedUser) {
        User owner;


        owner = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Address address = addressMapper.toEntity(dto.address());
        Barbershop barbershop = barbershopMapper.toEntity(dto, owner);
        barbershop.setAddress(address);

        this.barbershopRepository.save(barbershop);

        Member m = new Member();
        m.setBarbershop(barbershop);
        m.setUser(barbershop.getOwner());
        m.setRole(MemberRole.OWNER);

        this.memberRepository.save(m);

        String createdAt = barbershop.getCreatedAt()
                        .atZone(ZoneId.of(barbershop.getTimeZone()))
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        eventPublisher.publishEvent(new BarbershopCreatedEvent(
                barbershop.getOwner().getEmail(),
                barbershop.getOwner().getName(),
                barbershop.getName(),
                createdAt));

        return barbershop;
    }

    @PreAuthorize("@barbershopAccess.canManage(#barbershopId, principal)")
    public BarbershopResponseDTO updateBarbershop(UUID barbershopId, UpdateBarbershopDTO dto) {
        Barbershop barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new EntityNotFoundException("Barbershop not found"));

        if (dto.timeZone() != null) {
            if (!ZoneId.getAvailableZoneIds().contains(dto.timeZone()))
                throw new InvalidTimeZoneException(dto.timeZone());
            barbershop.setTimeZone(dto.timeZone());
        }

        barbershop.setName(dto.name());
        barbershop.setSlug(dto.slug());
        barbershop.setPhone(dto.phone());
        if (dto.logoUrl() != null && !dto.logoUrl().isBlank())
            barbershop.setLogoUrl(dto.logoUrl());
        addressMapper.updateEntity(dto.address(), barbershop.getAddress());

        Barbershop saved = barbershopRepository.save(barbershop);

        return barbershopMapper.toBarbershopResponseDTO(saved);
    }

    public List<BarbershopResponseDTO> getList(User user) {
        List<Barbershop> barbershops = this.barbershopRepository.findBarbershopByOwner(user);

        return barbershopMapper.toBarbershopResponseDTOList(barbershops);
    }
}