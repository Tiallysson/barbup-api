package com.barbup.barbup_api.repositories;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BarbershopRepository extends JpaRepository<Barbershop, UUID> {
}
