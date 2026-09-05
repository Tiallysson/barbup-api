package com.barbup.barbup_api.shared.dto.auth;

import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.domain.entity.user.UserRole;

import java.util.UUID;

public record UserCreatedResponseDTO(UUID id, String name, String email, UserRole role) {
    public UserCreatedResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
