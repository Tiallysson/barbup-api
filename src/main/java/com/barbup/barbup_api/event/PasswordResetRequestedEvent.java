package com.barbup.barbup_api.event;

import com.barbup.barbup_api.domain.entity.user.User;

import java.time.Duration;

public record PasswordResetRequestedEvent(
        User user,
        String email,
        String firstName,
        String code,
        Duration ttl
) {}
