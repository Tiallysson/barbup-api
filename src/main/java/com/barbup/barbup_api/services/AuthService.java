package com.barbup.barbup_api.services;

import lombok.RequiredArgsConstructor;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.infra.event.UserCreatedEvent;
import com.barbup.barbup_api.infra.event.VerificationCodeRequestedEvent;
import com.barbup.barbup_api.infra.ratelimit.RateLimitPlan;
import com.barbup.barbup_api.infra.ratelimit.RateLimiter;
import com.barbup.barbup_api.shared.dto.auth.ConfirmEmailRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.RegisterRequestDTO;
import com.barbup.barbup_api.shared.exception.EmailAlreadyExistsException;
import com.barbup.barbup_api.shared.exception.EmailAlreadyVerifiedException;
import com.barbup.barbup_api.shared.exception.InvalidVerificationCodeException;
import com.barbup.barbup_api.infra.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final SecureRandom CODE_GENERATOR = new SecureRandom();
    private static final long VERIFICATION_CODE_VALIDITY_MINUTES = 15;
    private static final long RESEND_COOLDOWN_SECONDS = 60;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final RateLimiter rateLimiter;

    public User register(RegisterRequestDTO body) {
        if (this.userRepository.findByEmail(body.email()).isPresent())
            throw new EmailAlreadyExistsException(body.email());

        User user = new User(body);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALIDITY_MINUTES));

        this.userRepository.save(user);

        eventPublisher.publishEvent(new UserCreatedEvent(user));
        log.info("Message published");

        return user;
    }

    public void confirmEmail(ConfirmEmailRequestDTO body) {
        User user = (User) this.userRepository.findByEmail(body.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isEmailVerified())
            throw new EmailAlreadyVerifiedException(body.email());

        boolean codeMatches = user.getVerificationCode() != null && user.getVerificationCode().equals(body.code());
        boolean codeExpired = user.getVerificationCodeExpiresAt() == null
                || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now());

        if (!codeMatches || codeExpired)
            throw new InvalidVerificationCodeException();

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);

        this.userRepository.save(user);
    }

    public void resendVerification(String email) {
        if (!rateLimiter.allow(RateLimitPlan.RESEND_VERIFICATION_EMAIL, email))
            return;

        this.userRepository.findByEmail(email)
                .map(userDetails -> (User) userDetails)
                .filter(user -> !user.isEmailVerified())
                .filter(this::cooldownElapsed)
                .ifPresent(user -> {
                    user.setVerificationCode(generateVerificationCode());
                    user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALIDITY_MINUTES));
                    this.userRepository.save(user);

                    eventPublisher.publishEvent(new VerificationCodeRequestedEvent(user));
                });
    }

    private boolean cooldownElapsed(User user) {
        if (user.getVerificationCodeExpiresAt() == null)
            return true;

        LocalDateTime lastSentAt = user.getVerificationCodeExpiresAt().minusMinutes(VERIFICATION_CODE_VALIDITY_MINUTES);
        return LocalDateTime.now().isAfter(lastSentAt.plusSeconds(RESEND_COOLDOWN_SECONDS));
    }

    private String generateVerificationCode() {
        int code = CODE_GENERATOR.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
