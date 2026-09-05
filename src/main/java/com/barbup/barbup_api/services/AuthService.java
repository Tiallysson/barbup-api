package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.dto.auth.ConfirmEmailRequestDTO;
import com.barbup.barbup_api.shared.dto.auth.RegisterRequestDTO;
import com.barbup.barbup_api.shared.exception.EmailAlreadyExistsException;
import com.barbup.barbup_api.shared.exception.EmailAlreadyVerifiedException;
import com.barbup.barbup_api.shared.exception.InvalidVerificationCodeException;
import com.barbup.barbup_api.infra.services.EmailService;
import com.barbup.barbup_api.infra.services.EmailTemplateRenderer;
import com.barbup.barbup_api.infra.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {
    private static final SecureRandom CODE_GENERATOR = new SecureRandom();
    private static final long VERIFICATION_CODE_VALIDITY_MINUTES = 15;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;

    public User register(RegisterRequestDTO body) {
        if (this.userRepository.findByEmail(body.email()).isPresent())
            throw new EmailAlreadyExistsException(body.email());

        User user = new User(body);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALIDITY_MINUTES));

        this.userRepository.save(user);

        sendVerificationEmail(user);

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

    private void sendVerificationEmail(User user) {
        String htmlBody = emailTemplateRenderer.render("email/verification", Map.of(
                "username", user.getName(),
                "verificationCode", user.getVerificationCode(),
                "expirationMinutes", VERIFICATION_CODE_VALIDITY_MINUTES
        ));

        this.emailService.sendMail(user.getEmail(), "Confirme seu cadastro - Barbup", htmlBody);
    }

    private String generateVerificationCode() {
        int code = CODE_GENERATOR.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
