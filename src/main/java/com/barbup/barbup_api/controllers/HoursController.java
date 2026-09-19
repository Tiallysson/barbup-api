package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.services.BusinessHoursService;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourDto;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/barbershop/hours")
@RequiredArgsConstructor
public class HoursController {
    private final BusinessHoursService businessHoursService;

    @PostMapping()
    public ResponseEntity<BusinessHourResponseDTO> createHours(@RequestBody @Valid BusinessHourDto body, @AuthenticationPrincipal User userAuthenticated) {
        BusinessHours businessHours = businessHoursService.createBusinessHour(body, userAuthenticated);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BusinessHourResponseDTO(businessHours));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<BusinessHourResponseDTO>> getBarbershopServiceHour(@PathVariable UUID id) {
        List<BusinessHourResponseDTO> hours = businessHoursService.getByBarbershopId(id);
        return ResponseEntity.ok(hours);
    }
}
