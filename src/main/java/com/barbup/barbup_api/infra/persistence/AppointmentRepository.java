package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.appointment.Appointment;
import com.barbup.barbup_api.domain.entity.appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.barber.id IN :barberIds
              AND a.status NOT IN :ignoredStatuses
              AND a.scheduledAt >= :from
              AND a.scheduledAt < :to
            """)
    List<Appointment> findBlockingByBarbers(@Param("barberIds") Collection<UUID> barberIds,
                                            @Param("ignoredStatuses") Collection<AppointmentStatus> ignoredStatuses,
                                            @Param("from") Instant from,
                                            @Param("to") Instant to);
}
