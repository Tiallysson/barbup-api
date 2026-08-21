package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.barbershop.Barbershop;
import com.barbup.barbup_api.domain.barbershop.dto.BarbershopResponseDTO;
import com.barbup.barbup_api.domain.barbershop.dto.CreateBarbershopDTO;
import com.barbup.barbup_api.domain.user.User;
import com.barbup.barbup_api.repositories.BarbershopRepository;
import com.barbup.barbup_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarbershopService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BarbershopRepository barbershopRepository;

    public Barbershop createBarbershop(CreateBarbershopDTO dto) {
        Barbershop b = new Barbershop();
        b.setAddress(dto.address());
        b.setCity(dto.city());
        b.setDocument(dto.document());
        b.setLogoUrl(dto.logoUrl());
        b.setName(dto.name());
        b.setSlug(dto.slug());
        b.setPhone(dto.phone());
        b.setState(dto.state());
        b.setZipcode(dto.zipcode());

        if (dto.userId() == null || dto.userId().isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal();
            b.setOwner(authenticatedUser);
        } else {
            User owner = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            b.setOwner(owner);
        }

        this.barbershopRepository.save(b);

        return b;
    }
}