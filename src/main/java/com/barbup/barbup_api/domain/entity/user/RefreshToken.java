package com.barbup.barbup_api.domain.entity.user;

import com.barbup.barbup_api.domain.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String tokenHash;
    @Column(nullable = false)
    private String deviceId;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false, columnDefinition = "FALSE")
    private boolean revoked;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
