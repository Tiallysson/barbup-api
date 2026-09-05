package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.shared.dto.DefaultReponse;
import com.barbup.barbup_api.shared.dto.password.ForgotPasswordRequest;
import com.barbup.barbup_api.shared.dto.password.ResetTokenResponse;
import com.barbup.barbup_api.shared.dto.password.VerifyCodeRequest;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.dto.auth.ConfirmEmailRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.LoginRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.RegisterRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.ResponseDTO;
import com.barbup.barbup_api.shared.dto.auth.UserCreatedResponseDTO;
import com.barbup.barbup_api.infra.security.TokenService;
import com.barbup.barbup_api.services.PasswordResetService;
import com.barbup.barbup_api.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDTO body) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(body.email(), body.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        User user = (User) auth.getPrincipal();
        Instant expiresAt = this.tokenService.generateExpirationDate();
        String token = this.tokenService.generateToken(user, expiresAt);

        return ResponseEntity.ok(new ResponseDTO(token, user.getRole(), expiresAt));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequestDTO body) {
        User user = this.authService.register(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserCreatedResponseDTO(user));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity confirmEmail(@RequestBody @Valid ConfirmEmailRequestDTO body) {
        this.authService.confirmEmail(body);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<DefaultReponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest body) {
        passwordResetService.requestReset(body.email());
        return ResponseEntity.ok(new DefaultReponse("Sent with successful"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ResetTokenResponse> verifyCode(@RequestBody @Valid VerifyCodeRequest body) {
        String resetToken = passwordResetService.verifyCode(body.email(), body.code());
        return ResponseEntity.ok(new ResetTokenResponse(resetToken, 600));
    }
}
