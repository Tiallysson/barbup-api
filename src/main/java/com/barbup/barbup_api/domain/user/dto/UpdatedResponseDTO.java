package com.barbup.barbup_api.domain.user.dto;

import com.barbup.barbup_api.domain.user.User;

public record UpdatedResponseDTO(String id, String name, String email, String phone) {
    public UpdatedResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
