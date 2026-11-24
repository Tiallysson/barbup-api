package com.barbup.barbup_api.controllers.client;

import com.barbup.barbup_api.domain.entity.service.ServiceResponse;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.services.AppointmentService;
import com.barbup.barbup_api.services.BusinessHoursService;
import com.barbup.barbup_api.services.ServiceService;
import com.barbup.barbup_api.shared.dto.appointment.AppointmentResponseDTO;
import com.barbup.barbup_api.shared.dto.appointment.AvailabilityResponseDTO;
import com.barbup.barbup_api.shared.dto.appointment.CreateAppointmentDTO;
import com.barbup.barbup_api.shared.dto.schedule.BusinessHourResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client/barbershops/{barbershopId}")
@RequiredArgsConstructor
public class ClientBarbershopController {
    private final ServiceService serviceService;
    private final BusinessHoursService businessHoursService;
    private final AppointmentService appointmentService;

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getServices(@PathVariable UUID barbershopId) {
        return ResponseEntity.ok(serviceService.getServices(barbershopId));
    }

    @GetMapping("/hours")
    public ResponseEntity<List<BusinessHourResponseDTO>> getHours(@PathVariable UUID barbershopId) {
        return ResponseEntity.ok(businessHoursService.getByBarbershopId(barbershopId));
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponseDTO> getAvailability(@PathVariable UUID barbershopId,
                                                                   @RequestParam UUID serviceId,
                                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                   @RequestParam(required = false) UUID barberId) {
        return ResponseEntity.ok(appointmentService.getAvailability(barbershopId, serviceId, date, barberId));
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponseDTO> book(@PathVariable UUID barbershopId,
                                                       @AuthenticationPrincipal User user,
                                                       @RequestBody @Valid CreateAppointmentDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(barbershopId, user, body));
    }
}
