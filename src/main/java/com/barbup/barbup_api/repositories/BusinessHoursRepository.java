package com.barbup.barbup_api.repositories;

import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.UUID;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, UUID> {
    Optional<BusinessHours> findByBarbershopIdAndDayOfWeek(UUID barbershopId, DayOfWeek dayOfWeek);
}
