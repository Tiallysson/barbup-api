package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, UUID> {
    @Query("""
            SELECT COUNT(b) > 0 FROM BusinessHours b
            WHERE b.barbershop.id = :barbershopId
              AND b.dayOfWeek = :dayOfWeek
              AND b.openTime < :closeTime
              AND b.closeTime > :openTime
            """)
    boolean existsOverlapping(@Param("barbershopId") UUID barbershopId,
                              @Param("dayOfWeek") DayOfWeek dayOfWeek,
                              @Param("openTime") LocalTime openTime,
                              @Param("closeTime") LocalTime closeTime);

    @Query("""
            SELECT COUNT(b) > 0 FROM BusinessHours b
            WHERE b.barbershop.id = :barbershopId
              AND b.dayOfWeek = :dayOfWeek
              AND b.id  <> :excludeId
              AND b.openTime < :closeTime
              AND b.closeTime > :openTime
            """)
    boolean existsOverlappingExcluding(@Param("barbershopId") UUID barbershopId,
                                       @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                       @Param("openTime") LocalTime openTime,
                                       @Param("closeTime") LocalTime closeTime,
                                       @Param("excludeId") UUID excludeId);

    Optional<List<BusinessHours>> findAllByBarbershopId(UUID barbershopId);

    Optional<BusinessHours> findByIdAndBarbershopId(UUID id, UUID barbershopId);
}
