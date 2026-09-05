package com.barbup.barbup_api.domain.entity.password;

import com.barbup.barbup_api.domain.abstracts.BaseEntity;
import com.barbup.barbup_api.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "password_reset_code")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetCode extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String codeHash;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private Integer attempts = 0;

    private Instant consumedAt;

    public PasswordResetCode(User user, String codeHash, Duration ttl) {
        this.user = user;
        this.codeHash = codeHash;
        this.expiresAt = Instant.now().plus(ttl);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public boolean isUsable() {
        return !isExpired() && !isConsumed() && attempts < 5;
    }

    public void registerFailedAttempt() {
        this.attempts++;
    }

    public void consume() {
        this.consumedAt = Instant.now();
    }

}
