package com.barbup.barbup_api.domain.entity.user.dto;

import com.barbup.barbup_api.domain.entity.user.User;

import java.util.UUID;

public record UpdatedResponseDTO(UUID id, String name, String email, String phone) {
    public UpdatedResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
