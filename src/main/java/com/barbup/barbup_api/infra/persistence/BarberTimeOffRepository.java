package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.schedule.BarberTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BarberTimeOffRepository extends JpaRepository<BarberTimeOff, UUID> {
    @Query("""
            SELECT t FROM BarberTimeOff t
            WHERE t.barber.id IN :barberIds
              AND t.startAt < :to
              AND t.endAt > :from
            """)
    List<BarberTimeOff> findOverlapping(@Param("barberIds") Collection<UUID> barberIds,
                                        @Param("from") Instant from,
                                        @Param("to") Instant to);
}
