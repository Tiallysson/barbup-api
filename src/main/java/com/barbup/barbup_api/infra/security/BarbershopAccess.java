package com.barbup.barbup_api.infra.security;

import com.barbup.barbup_api.domain.entity.member.MemberRole;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.infra.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Component("barbershopAccess")
@RequiredArgsConstructor
public class BarbershopAccess {

    private static final Set<MemberRole> MANAGERS = EnumSet.of(MemberRole.OWNER);
    private static final Set<MemberRole> ALL_MEMBERS = EnumSet.allOf(MemberRole.class);

    private final MemberRepository memberRepository;

    public boolean isMember(UUID barbershopId, Object principal) {
        return hasRole(barbershopId, principal, ALL_MEMBERS);
    }

    public boolean canManage(UUID barbershopId, Object principal) {
        return hasRole(barbershopId, principal, MANAGERS);
    }

    public boolean canManageService(UUID serviceId, Object principal) {
        if (serviceId == null || !(principal instanceof User user)) {
            return false;
        }
        return memberRepository.existsByServiceIdAndUserIdAndRoleIn(serviceId, user.getId(), MANAGERS);
    }

    private boolean hasRole(UUID barbershopId, Object principal, Set<MemberRole> roles) {
        if (barbershopId == null || !(principal instanceof User user)) {
            return false;
        }
        return memberRepository.existsByBarbershopIdAndUserIdAndRoleIn(barbershopId, user.getId(), roles);
    }

    public boolean canManageBusinessHour(UUID businessHourId, Object principal) {
        if (businessHourId == null || !(principal instanceof User user)) {
            return false;
        }
        return memberRepository.existsByBusinessHourIdAndUserIdAndRoleIn(businessHourId, user.getId(), MANAGERS);
    }
}
