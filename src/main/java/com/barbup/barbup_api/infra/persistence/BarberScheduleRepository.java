package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.schedule.BarberSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BarberScheduleRepository extends JpaRepository<BarberSchedule, UUID> {
    List<BarberSchedule> findAllByBarberIdIn(Collection<UUID> barberIds);

    List<BarberSchedule> findAllByBarberIdInAndDayOfWeek(Collection<UUID> barberIds, DayOfWeek dayOfWeek);
}
