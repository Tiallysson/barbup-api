package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.entity.service.ServiceRegister;
import com.barbup.barbup_api.domain.entity.service.ServiceResponse;
import com.barbup.barbup_api.domain.entity.service.ServiceUpdate;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.services.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/barbershop/service")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;

    @GetMapping("/by-barbershop/{id}")
    public ResponseEntity<List<ServiceResponse>> getServices(@PathVariable UUID id) {
        List<ServiceResponse> services = serviceService.getServices(id);
        return ResponseEntity.status(HttpStatus.OK).body(services);
    }

    @PostMapping()
    public ResponseEntity<ServiceResponse> createService(@RequestBody @Valid ServiceRegister body, @AuthenticationPrincipal User userAuthenticated) {
        ServiceResponse response = serviceService.createService(body, userAuthenticated);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ServiceResponse> updateService(@RequestBody @Valid ServiceUpdate body, @AuthenticationPrincipal User userAuthenticated) {
        ServiceResponse response = serviceService.updateService(body, userAuthenticated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteService(@PathVariable UUID id, @AuthenticationPrincipal User userAuthenticated) {
        serviceService.deleteService(id, userAuthenticated);
        return ResponseEntity.ok().build();
    }
}
