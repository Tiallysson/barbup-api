package com.barbup.barbup_api.repositories;

import com.barbup.barbup_api.domain.barbershop.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByBarbershopIdAndUserId(String barbershopId, String userId);
}
