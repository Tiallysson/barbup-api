package com.barbup.barbup_api.infra.event.listener;

import com.barbup.barbup_api.infra.email.EmailService;
import com.barbup.barbup_api.infra.email.EmailTemplateRenderer;
import com.barbup.barbup_api.infra.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.infra.event.UserCreatedEvent;
import com.barbup.barbup_api.infra.event.VerificationCodeRequestedEvent;
import com.barbup.barbup_api.domain.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Component
public class UserCreatedNotificationListener {
    private static final String SUBJECT = "Confirme seu cadastro - Barbup";
    private static final long VERIFICATION_CODE_VALIDITY_MINUTES = 15;

    private final EmailService emailService;
    private final EmailTemplateRenderer renderer;

    public UserCreatedNotificationListener(EmailService emailService, EmailTemplateRenderer renderer) {
        this.emailService = emailService;
        this.renderer = renderer;
    }

    @Async
    @EventListener
    public void on(UserCreatedEvent event) {
        sendVerificationEmail(event.user());
    }

    @Async
    @EventListener
    public void on(VerificationCodeRequestedEvent event) {
        sendVerificationEmail(event.user());
    }

    private void sendVerificationEmail(User user) {
        try {
            String htmlBody = renderer.render("email/verification", Map.of(
                    "username", user.getName(),
                    "verificationCode", user.getVerificationCode(),
                    "expirationMinutes", VERIFICATION_CODE_VALIDITY_MINUTES
            ));

            emailService.sendMail(user.getEmail(), SUBJECT, htmlBody);
        } catch (Exception e) {
            log.error("Error while send email. {}", e.getMessage());
        }
    }
}
