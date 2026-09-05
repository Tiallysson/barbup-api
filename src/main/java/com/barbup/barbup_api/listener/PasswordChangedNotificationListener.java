package com.barbup.barbup_api.listener;

import com.barbup.barbup_api.event.PasswordChangedEvent;
import com.barbup.barbup_api.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.infra.services.EmailService;
import com.barbup.barbup_api.infra.services.EmailTemplateRenderer;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;

@Component
public class PasswordChangedNotificationListener {
    private static final String SUBJECT = "Sua senha foi alterada - Barbup";

    private final EmailService emailService;
    private final EmailTemplateRenderer renderer;

    public PasswordChangedNotificationListener(EmailService emailService, EmailTemplateRenderer renderer) {
        this.emailService = emailService;
        this.renderer = renderer;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PasswordChangedEvent event) {

        String htmlBody = renderer.render("email/password_changed", Map.of(
                "username", event.firstName(),
                "changedAt", Instant.now()
        ));

        emailService.sendMail(event.email(), SUBJECT, htmlBody);
    }
}
