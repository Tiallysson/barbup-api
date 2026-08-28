package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.barbershop.Barbershop;
import com.barbup.barbup_api.domain.barbershop.dto.BarbershopResponseDTO;
import com.barbup.barbup_api.domain.barbershop.dto.CreateBarbershopDTO;
import com.barbup.barbup_api.domain.schedule.BusinessHours;
import com.barbup.barbup_api.domain.schedule.dto.BusinessHourDto;
import com.barbup.barbup_api.domain.schedule.dto.BusinessHourResponseDTO;
import com.barbup.barbup_api.domain.user.User;
import com.barbup.barbup_api.services.BarbershopService;
import com.barbup.barbup_api.services.BusinessHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/barbershop")
@RequiredArgsConstructor
public class BarbershopController {
    private final BarbershopService barbershopService;
    private final BusinessHoursService businessHoursService;

    @PostMapping("/create")
    public ResponseEntity<BarbershopResponseDTO> create(@RequestBody @Validated CreateBarbershopDTO body) {
        Barbershop barbershop = barbershopService.createBarbershop(body);
        return ResponseEntity.ok(new BarbershopResponseDTO(barbershop));
    }

    @PostMapping("/hours")
    public ResponseEntity<BusinessHourResponseDTO> createHours(@RequestBody @Validated BusinessHourDto body, @AuthenticationPrincipal User userAuthenticated) {
        BusinessHours businessHours = businessHoursService.createBusinessHour(body, userAuthenticated);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BusinessHourResponseDTO(businessHours));
    }
}
