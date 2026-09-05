package com.barbup.barbup_api.shared.dto.auth;

import com.barbup.barbup_api.domain.entity.user.UserRole;

import java.time.Instant;

public record ResponseDTO(String token, UserRole role, Instant expiresAt) {
}
