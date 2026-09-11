package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHashAndUserIdAndRevokedFalse(String tokenHash, UUID userId);
}
