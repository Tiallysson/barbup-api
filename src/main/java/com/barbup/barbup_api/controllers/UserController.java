package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.entity.user.dto.UpdateRequestDTO;
import com.barbup.barbup_api.domain.entity.user.dto.UpdatedResponseDTO;
import com.barbup.barbup_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody @Validated UpdateRequestDTO body) {
        var user = this.service.updateUser(body);
        return ResponseEntity.ok(new UpdatedResponseDTO(user));
    }
}
