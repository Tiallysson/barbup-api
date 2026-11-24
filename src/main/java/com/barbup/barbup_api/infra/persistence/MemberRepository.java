package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.member.Member;
import com.barbup.barbup_api.domain.entity.member.MemberRole;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByBarbershopIdAndUserId(UUID barbershopId, UUID userId);

    boolean existsByBarbershopIdAndUserIdAndRoleIn(UUID barbershopId, UUID userId, Collection<MemberRole> roles);

    List<Member> findAllByBarbershopIdAndRoleIn(UUID barbershopId, Collection<MemberRole> roles);

    Optional<Member> findByIdAndBarbershopIdAndRoleIn(UUID id, UUID barbershopId, Collection<MemberRole> roles);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Member m WHERE m.id = :id")
    Optional<Member> lockById(@Param("id") UUID id);
}
