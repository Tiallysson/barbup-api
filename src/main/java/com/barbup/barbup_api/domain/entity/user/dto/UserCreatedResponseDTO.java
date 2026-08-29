package com.barbup.barbup_api.domain.entity.user.dto;

import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.domain.entity.user.UserRole;

public record UserCreatedResponseDTO(String id, String name, String email, UserRole role) {
    public UserCreatedResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
