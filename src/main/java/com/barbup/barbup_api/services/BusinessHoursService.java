package com.barbup.barbup_api.services;

import lombok.RequiredArgsConstructor;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourDto;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourResponseDTO;
import com.barbup.barbup_api.shared.exception.BusinessHourConflictException;
import com.barbup.barbup_api.shared.exception.InvalidBusinessHourException;
import com.barbup.barbup_api.infra.persistence.BarbershopRepository;
import com.barbup.barbup_api.infra.persistence.BusinessHoursRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessHoursService {
    private final BusinessHoursRepository businessHoursRepository;
    private final BarbershopRepository barbershopRepository;

    @PreAuthorize("@barbershopAccess.canManage(#dto.barbershopId(), principal)")
    public BusinessHours createBusinessHour(BusinessHourDto dto, User authenticatedUser) {
        Barbershop barbershop = barbershopRepository.findById(dto.barbershopId())
                .orElseThrow(() -> new EntityNotFoundException("Barbershop not found"));

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

    public List<BusinessHourResponseDTO> getByBarbershopId(UUID id) {
        List<BusinessHours> hours = businessHoursRepository.findAllByBarbershopId(id).orElseThrow();

        return hours.stream()
                .map(p -> new BusinessHourResponseDTO(
                        p.getId(),
                        p.getBarbershop().getId(),
                        p.getDayOfWeek(),
                        p.getOpenTime(),
                        p.getCloseTime())
                )
                .collect(Collectors.toList());
    }
}
