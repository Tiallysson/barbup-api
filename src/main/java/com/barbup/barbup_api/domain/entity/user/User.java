package com.barbup.barbup_api.domain.entity.user;

import com.barbup.barbup_api.domain.abstracts.BaseEntity;
import com.barbup.barbup_api.shared.dto.auth.RegisterRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(length = 14)
    private String phone;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, columnDefinition = "smallint default 0")
    private UserRole role;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean emailVerified = false;
    @Column(length = 6)
    private String verificationCode;
    private LocalDateTime verificationCodeExpiresAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN)
            return  List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.emailVerified;
    }

    public User(RegisterRequestDTO registerRequest) {
        this.email = registerRequest.email();
        this.phone = registerRequest.phone();
        this.name = registerRequest.name();
        this.role = UserRole.USER;
    }
}
