package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.member.Member;
import com.barbup.barbup_api.domain.entity.member.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByBarbershopIdAndUserId(UUID barbershopId, UUID userId);

    boolean existsByBarbershopIdAndUserIdAndRoleIn(UUID barbershopId, UUID userId, Collection<MemberRole> roles);

    @Query("""
            select count(m) > 0
            from Member m, Services s
            where s.id = :serviceId
              and m.barbershop = s.barbershop
              and m.user.id = :userId
              and m.role in :roles
            """)
    boolean existsByServiceIdAndUserIdAndRoleIn(@Param("serviceId") UUID serviceId,
                                                @Param("userId") UUID userId,
                                                @Param("roles") Collection<MemberRole> roles);

    @Query("""
            select count(m) > 0
            from Member m, BusinessHours b
            where b.id = :businessHourId
            and m.barbershop = b.barbershop
            and m.user.id = :userId
            and m.role in :roles
            """)
    boolean existsByBusinessHourIdAndUserIdAndRoleIn(@Param("businessHourId") UUID businessHourId,
                                                     @Param("userId") UUID userId,
                                                     @Param("roles")Collection<MemberRole> roles);
}
