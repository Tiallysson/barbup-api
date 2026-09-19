package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.service.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Services, UUID> {
    List<Services> getAllByActiveTrueAndBarbershop_Id(UUID barbershop_id);
}
