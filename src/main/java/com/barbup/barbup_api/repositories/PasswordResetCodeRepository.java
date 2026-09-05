package com.barbup.barbup_api.repositories;

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
        UPDATE password_reset_code c
           SET c.consumed_at = :now
         WHERE c.user_id = :userId
           AND c.consumed_at IS NULL
           AND c.expires_at > :now
    """)
    int invalidateActiveCodes(@Param("userId") UUID userId, @Param("now") Instant now);

    Optional<PasswordResetCode> findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(UUID userId);
}
