package com.barbup.barbup_api.controllers;

import com.barbup.barbup_api.domain.user.User;
import com.barbup.barbup_api.domain.user.UserRole;
import com.barbup.barbup_api.domain.user.dto.LoginRequestDTO;
import com.barbup.barbup_api.domain.user.dto.RegisterRequestDTO;
import com.barbup.barbup_api.domain.user.dto.ResponseDTO;
import com.barbup.barbup_api.infra.security.TokenService;
import com.barbup.barbup_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginRequestDTO body) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(body.email(), body.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        String token = this.tokenService.generateToken((User) auth.getPrincipal());

        return  ResponseEntity.ok(new ResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Validated RegisterRequestDTO body) {
        if (this.repository.findByEmail(body.email()) != null)
            return ResponseEntity.badRequest().build();

        User user = new User();
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setEmail(body.email());
        user.setPhone(body.phone());
        user.setName(body.name());
        user.setRole(UserRole.USER);

        this.repository.save(user);

        String token = this.tokenService.generateToken(user);
        return ResponseEntity.ok(new ResponseDTO(token));
    }
}
