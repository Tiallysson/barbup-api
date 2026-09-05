package com.barbup.barbup_api.infra.event.listener;

import com.barbup.barbup_api.infra.email.EmailService;
import com.barbup.barbup_api.infra.email.EmailTemplateRenderer;
import com.barbup.barbup_api.infra.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.infra.event.UserCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserCreatedEvent event) {
        String htmlBody = renderer.render("email/verification", Map.of(
                "username", event.user().getName(),
                "verificationCode", event.user().getVerificationCode(),
                "expirationMinutes", VERIFICATION_CODE_VALIDITY_MINUTES
        ));

        emailService.sendMail(event.user().getEmail(), SUBJECT, htmlBody);
    }
}
