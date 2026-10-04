package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.dto.auth.UpdateRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.UpdatedResponseDTO;
import com.barbup.barbup_api.services.UserService;
import com.barbup.barbup_api.shared.dto.auth.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody @Valid UpdateRequestDTO body, @AuthenticationPrincipal User userAuthenticated) {
        var user = this.service.updateUser(body, userAuthenticated);
        return ResponseEntity.ok(new UpdatedResponseDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal User userAuthenticated) {
        return ResponseEntity.ok(new UserResponseDTO(userAuthenticated));
    }
}
