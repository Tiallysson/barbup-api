package com.barbup.barbup_api.domain.entity.user.dto;

import com.barbup.barbup_api.domain.entity.user.UserRole;

import java.time.Instant;

public record ResponseDTO(String token, UserRole role, Instant expiresAt) {
}
