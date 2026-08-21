package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.barbershop.Barbershop;
import com.barbup.barbup_api.domain.barbershop.dto.BarbershopResponseDTO;
import com.barbup.barbup_api.domain.barbershop.dto.CreateBarbershopDTO;
import com.barbup.barbup_api.services.BarbershopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barbershop")
@RequiredArgsConstructor
public class BarbershopController {
    private final BarbershopService service;

    @PostMapping("/create")
    public ResponseEntity<BarbershopResponseDTO> create(@RequestBody CreateBarbershopDTO body) {
        try {
            var barbershop = service.createBarbershop(body);
            return ResponseEntity.ok(new BarbershopResponseDTO(barbershop));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
