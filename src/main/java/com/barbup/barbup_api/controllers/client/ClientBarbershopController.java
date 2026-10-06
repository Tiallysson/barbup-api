package com.barbup.barbup_api.controllers.client;

import com.barbup.barbup_api.domain.entity.service.ServiceResponse;
import com.barbup.barbup_api.services.BusinessHoursService;
import com.barbup.barbup_api.services.ServiceService;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client/barbershops/{barbershopId}")
@RequiredArgsConstructor
public class ClientBarbershopController {
    private final ServiceService serviceService;
    private final BusinessHoursService businessHoursService;

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getServices(@PathVariable UUID barbershopId) {
        return ResponseEntity.ok(serviceService.getServices(barbershopId));
    }

    @GetMapping("/hours")
    public ResponseEntity<List<BusinessHourResponseDTO>> getHours(@PathVariable UUID barbershopId) {
        return ResponseEntity.ok(businessHoursService.getByBarbershopId(barbershopId));
    }
}
