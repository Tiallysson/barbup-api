package com.barbup.barbup_api.controllers.account;

import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.dto.auth.UpdateRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.UpdatedResponseDTO;
import com.barbup.barbup_api.services.UserService;
import com.barbup.barbup_api.shared.dto.auth.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class AccountController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal User userAuthenticated) {
        return ResponseEntity.ok(new UserResponseDTO(userAuthenticated));
    }

    @PutMapping
    public ResponseEntity<UpdatedResponseDTO> update(@RequestBody @Valid UpdateRequestDTO body, @AuthenticationPrincipal User userAuthenticated) {
        var user = this.service.updateUser(body, userAuthenticated);
        return ResponseEntity.ok(new UpdatedResponseDTO(user));
    }
}
