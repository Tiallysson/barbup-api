package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.address.Address;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BarbershopService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BarbershopRepository barbershopRepository;
    @Autowired
    private MemberRepository memberRepository;

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

        return barbershop;
    }
}