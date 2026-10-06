package com.barbup.barbup_api.controllers.manage;

import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import com.barbup.barbup_api.services.BusinessHoursService;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourDto;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourResponseDTO;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manage/barbershops/{barbershopId}/hours")
@RequiredArgsConstructor
public class ManageHoursController {
    private final BusinessHoursService businessHoursService;

    @PostMapping
    public ResponseEntity<BusinessHourResponseDTO> create(@PathVariable UUID barbershopId, @RequestBody @Valid BusinessHourDto body) {
        BusinessHours businessHours = businessHoursService.createBusinessHour(barbershopId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BusinessHourResponseDTO(businessHours));
    }

    @PutMapping("/{hourId}")
    public ResponseEntity<List<BusinessHourResponseDTO>> update(@PathVariable UUID barbershopId, @PathVariable UUID hourId, @RequestBody @Valid BusinessHourUpdateDTO body) {
        return ResponseEntity.ok(businessHoursService.updateBusinessHours(barbershopId, hourId, body));
    }
}
