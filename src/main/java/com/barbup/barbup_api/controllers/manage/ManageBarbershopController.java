package com.barbup.barbup_api.controllers.manage;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.services.BarbershopService;
import com.barbup.barbup_api.shared.dto.barbershop.BarbershopResponseDTO;
import com.barbup.barbup_api.shared.dto.barbershop.CreateBarbershopDTO;
import com.barbup.barbup_api.shared.dto.barbershop.UpdateBarbershopDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manage/barbershops")
@RequiredArgsConstructor
public class ManageBarbershopController {
    private final BarbershopService barbershopService;

    @GetMapping
    public ResponseEntity<List<BarbershopResponseDTO>> list(@AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(barbershopService.getList(authenticatedUser));
    }

    @PostMapping
    public ResponseEntity<BarbershopResponseDTO> create(@RequestBody @Valid CreateBarbershopDTO body, @AuthenticationPrincipal User authenticatedUser) {
        Barbershop barbershop = barbershopService.createBarbershop(body, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BarbershopResponseDTO(barbershop));
    }

    @PutMapping("/{barbershopId}")
    public ResponseEntity<BarbershopResponseDTO> update(@PathVariable UUID barbershopId, @RequestBody @Valid UpdateBarbershopDTO body) {
        return ResponseEntity.ok(barbershopService.updateBarbershop(barbershopId, body));
    }
}
