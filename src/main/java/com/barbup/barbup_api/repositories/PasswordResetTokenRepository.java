package com.barbup.barbup_api.repositories;

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
        UPDATE password_reset_tokens c
           SET c.used_at = :now
         WHERE c.user_id = :userId
           AND c.used_at IS NULL
           AND c.expires_at > :now
    """)
    int invalidateActiveTokens(@Param("userId") UUID userId, @Param("now") Instant now);
}
