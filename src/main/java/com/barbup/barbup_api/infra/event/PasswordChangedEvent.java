package com.barbup.barbup_api.infra.event;

import com.barbup.barbup_api.domain.entity.user.User;

public record PasswordChangedEvent(
        User user,
        String email,
        String firstName
) {
}
