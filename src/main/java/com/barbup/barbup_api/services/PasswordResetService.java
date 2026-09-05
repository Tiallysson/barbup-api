package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.password.PasswordResetCode;
import com.barbup.barbup_api.domain.entity.password.PasswordResetToken;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.event.PasswordChangedEvent;
import com.barbup.barbup_api.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.shared.exception.InvalidResetCodeException;
import com.barbup.barbup_api.shared.exception.InvalidResetTokenException;
import com.barbup.barbup_api.shared.exception.SamePasswordException;
import com.barbup.barbup_api.infra.security.ResetCodeGenerator;
import com.barbup.barbup_api.infra.persistence.PasswordResetCodeRepository;
import com.barbup.barbup_api.infra.persistence.PasswordResetTokenRepository;
import com.barbup.barbup_api.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;


@Service
public class PasswordResetService {
    private static final Duration TTL = Duration.ofMinutes(15);
    private final Logger log = (Logger) LoggerFactory.getLogger(PasswordResetService.class);
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);


    private final UserRepository userRepository;
    private final PasswordResetCodeRepository codeRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final ResetCodeGenerator generator;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, PasswordResetCodeRepository codeRepository, PasswordResetTokenRepository tokenRepository, ResetCodeGenerator generator, ApplicationEventPublisher eventPublisher, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.tokenRepository = tokenRepository;
        this.generator = generator;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void requestReset(String email) {
        User user = userRepository.findByEmail(email)
                .map(userDetails -> (User) userDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        codeRepository.invalidateActiveCodes(user.getId(), Instant.now());

        String code = generator.generate();
        String codeHash = generator.hash(code, user.getId());
        codeRepository.save(new PasswordResetCode(user, codeHash, TTL));

        eventPublisher.publishEvent(new PasswordResetRequestedEvent(user, email, user.getUsername(), code, TTL));
    }

    @Transactional
    public String verifyCode(String email, String rawCode) {
        User user = userRepository.findByEmail(email)
                .map(userDetails -> (User) userDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PasswordResetCode stored = codeRepository
                .findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId())
                .orElseThrow();

        if (stored.isExpired() || stored.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidResetCodeException("Invalid code");
        }

        String candidateHash = generator.hash(rawCode, user.getId());

        if (!MessageDigest.isEqual(
                candidateHash.getBytes(StandardCharsets.UTF_8),
                stored.getCodeHash().getBytes(StandardCharsets.UTF_8)
        )) {
            stored.registerFailedAttempt();
            codeRepository.save(stored);
            throw new InvalidResetCodeException("Invalid code");
        }

        stored.consume();
        codeRepository.save(stored);

        return issueResetToken(user);
    }

    @Transactional
    public void resetPassword(String rawCode, String newPassword, UUID userId) {
        PasswordResetToken token = tokenRepository.findByTokenHash(generator.hash(rawCode, userId))
                .filter(PasswordResetToken::isUsable)
                .orElseThrow(InvalidResetTokenException::new);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(InvalidResetTokenException::new);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new SamePasswordException();
        };

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.markUsed();
        tokenRepository.save(token);
        tokenRepository.invalidateActiveTokens(token.getUserId(), Instant.now());

        eventPublisher.publishEvent(new PasswordChangedEvent(
                user, user.getEmail(), user.getName()
        ));
    }

    public String issueResetToken(User user) {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        tokenRepository.save(new PasswordResetToken(
                user.getId(),
                generator.sha256(rawToken),
                Instant.now().plus(TOKEN_TTL)
        ));

        return rawToken;
    }
}
