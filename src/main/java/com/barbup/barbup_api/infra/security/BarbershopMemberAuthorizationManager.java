package com.barbup.barbup_api.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class BarbershopMemberAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final BarbershopAccess barbershopAccess;

    @Override
    public AuthorizationResult authorize(Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
        String rawId = context.getVariables().get("barbershopId");
        if (rawId == null) {
            return new AuthorizationDecision(false);
        }
        try {
            UUID barbershopId = UUID.fromString(rawId);
            return new AuthorizationDecision(barbershopAccess.isMember(barbershopId, authentication.get().getPrincipal()));
        } catch (IllegalArgumentException e) {
            return new AuthorizationDecision(false);
        }
    }
}
