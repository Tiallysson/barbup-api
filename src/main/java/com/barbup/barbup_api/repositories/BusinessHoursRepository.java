package com.barbup.barbup_api.repositories;

import com.barbup.barbup_api.domain.barbershop.schedule.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, String> {
    Optional<BusinessHours> findByBarbershopIdAndDayOfWeek(String barbershopId, DayOfWeek dayOfWeek);
}
