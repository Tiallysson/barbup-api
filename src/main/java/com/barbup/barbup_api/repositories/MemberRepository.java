package com.barbup.barbup_api.repositories;

import com.barbup.barbup_api.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByBarbershopIdAndUserId(UUID barbershopId, UUID userId);
}
