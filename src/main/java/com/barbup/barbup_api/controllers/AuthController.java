package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.domain.entity.user.dto.ConfirmEmailRequestDTO;
import com.barbup.barbup_api.domain.entity.user.dto.LoginRequestDTO;
import com.barbup.barbup_api.domain.entity.user.dto.RegisterRequestDTO;
import com.barbup.barbup_api.domain.entity.user.dto.ResponseDTO;
import com.barbup.barbup_api.domain.entity.user.dto.UserCreatedResponseDTO;
import com.barbup.barbup_api.infra.security.TokenService;
import com.barbup.barbup_api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginRequestDTO body) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(body.email(), body.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        User user = (User) auth.getPrincipal();
        Instant expiresAt = this.tokenService.generateExpirationDate();
        String token = this.tokenService.generateToken(user, expiresAt);

        return ResponseEntity.ok(new ResponseDTO(token, user.getRole(), expiresAt));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Validated RegisterRequestDTO body) {
        User user = this.authService.register(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserCreatedResponseDTO(user));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity confirmEmail(@RequestBody @Validated ConfirmEmailRequestDTO body) {
        this.authService.confirmEmail(body);
        return ResponseEntity.noContent().build();
    }
}
