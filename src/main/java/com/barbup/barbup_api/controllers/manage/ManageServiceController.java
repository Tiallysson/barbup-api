package com.barbup.barbup_api.controllers.manage;

import com.barbup.barbup_api.domain.entity.service.ServiceRegister;
import com.barbup.barbup_api.domain.entity.service.ServiceResponse;
import com.barbup.barbup_api.domain.entity.service.ServiceUpdate;
import com.barbup.barbup_api.services.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/manage/barbershops/{barbershopId}/services")
@RequiredArgsConstructor
public class ManageServiceController {
    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@PathVariable UUID barbershopId, @RequestBody @Valid ServiceRegister body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(barbershopId, body));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<ServiceResponse> update(@PathVariable UUID barbershopId, @PathVariable UUID serviceId, @RequestBody @Valid ServiceUpdate body) {
        return ResponseEntity.ok(serviceService.updateService(barbershopId, serviceId, body));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(@PathVariable UUID barbershopId, @PathVariable UUID serviceId) {
        serviceService.deleteService(barbershopId, serviceId);
        return ResponseEntity.noContent().build();
    }
}
