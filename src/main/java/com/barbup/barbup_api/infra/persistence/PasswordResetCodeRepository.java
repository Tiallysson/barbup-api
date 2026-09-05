package com.barbup.barbup_api.infra.persistence;

import com.barbup.barbup_api.domain.entity.password.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, UUID> {
    @Modifying
    @Query("""
        UPDATE PasswordResetCode c
               SET c.consumedAt = :now
             WHERE c.user.id = :userId
               AND c.consumedAt IS NULL
               AND c.expiresAt > :now
    """)
    int invalidateActiveCodes(@Param("userId") UUID userId, @Param("now") Instant now);

    Optional<PasswordResetCode> findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(UUID userId);
}
