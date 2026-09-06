package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BarbershopRepository extends JpaRepository<Barbershop, UUID> {
    List<Barbershop> findBarbershopByOwner(User owner);
}
