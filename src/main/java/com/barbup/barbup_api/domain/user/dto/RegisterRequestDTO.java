package com.barbup.barbup_api.domain.user.dto;

public record RegisterRequestDTO(String name, String email, String phone, String password) {
}
