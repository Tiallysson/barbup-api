package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourDto;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.exception.BusinessHourConflictException;
import com.barbup.barbup_api.shared.exception.InvalidBusinessHourException;
import com.barbup.barbup_api.repositories.BarbershopRepository;
import com.barbup.barbup_api.repositories.BusinessHoursRepository;
import com.barbup.barbup_api.repositories.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class BusinessHoursService {
    @Autowired
    private BusinessHoursRepository businessHoursRepository;
    @Autowired
    private BarbershopRepository barbershopRepository;
    @Autowired
    private MemberRepository memberRepository;

    public BusinessHours createBusinessHour(BusinessHourDto dto, User authenticatedUser) {
        Barbershop barbershop = barbershopRepository.findById(dto.barbershopId())
                .orElseThrow(() -> new EntityNotFoundException("Barbershop not found"));

        boolean isMember = memberRepository.existsByBarbershopIdAndUserId(barbershop.getId(), authenticatedUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of this barbershop");
        }

        if (!dto.openTime().isBefore(dto.closeTime())) {
            throw new InvalidBusinessHourException("Open time must be before close time");
        }

        businessHoursRepository.findByBarbershopIdAndDayOfWeek(barbershop.getId(), dto.dayOfWeek())
                .ifPresent(existing -> {
                    throw new BusinessHourConflictException(dto.dayOfWeek());
                });

        BusinessHours businessHours = new BusinessHours();
        businessHours.setBarbershop(barbershop);
        businessHours.setDayOfWeek(dto.dayOfWeek());
        businessHours.setOpenTime(dto.openTime());
        businessHours.setCloseTime(dto.closeTime());

        return businessHoursRepository.save(businessHours);
    }
}
