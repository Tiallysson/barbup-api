package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.password.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        UPDATE PasswordResetToken c
           SET c.usedAt = :now
         WHERE c.userId = :userId
           AND c.usedAt IS NULL
           AND c.expiresAt > :now
    """)
    int invalidateActiveTokens(@Param("userId") UUID userId, @Param("now") Instant now);
}
