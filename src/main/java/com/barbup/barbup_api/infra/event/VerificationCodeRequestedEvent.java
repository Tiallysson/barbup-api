package com.barbup.barbup_api.infra.event;

import com.barbup.barbup_api.domain.entity.user.User;

public record VerificationCodeRequestedEvent(User user) {
}
