package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.infra.event.BarbershopCreatedEvent;
import com.barbup.barbup_api.shared.dto.barbershop.BarbershopResponseDTO;
import com.barbup.barbup_api.shared.dto.barbershop.CreateBarbershopDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarbershopService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BarbershopRepository barbershopRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final AddressMapper addressMapper;
    private final BarbershopMapper barbershopMapper;

    public Barbershop createBarbershop(CreateBarbershopDTO dto) {
        User owner;

        if (dto.userId() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            owner = (User) authentication.getPrincipal();
        } else {
            owner = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

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
                        .atZone(ZoneId.of("America/Sao_Paulo"))
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        eventPublisher.publishEvent(new BarbershopCreatedEvent(
                barbershop.getOwner().getEmail(),
                barbershop.getOwner().getName(),
                barbershop.getName(),
                createdAt));

        return barbershop;
    }

    public List<BarbershopResponseDTO> getList(User user) {
        List<Barbershop> barbershops = this.barbershopRepository.findBarbershopByOwner(user);

        return barbershopMapper.toBarbershopResponseDTOList(barbershops);
    }
}