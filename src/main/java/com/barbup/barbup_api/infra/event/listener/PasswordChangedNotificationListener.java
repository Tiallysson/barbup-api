package com.barbup.barbup_api.infra.event.listener;

import com.barbup.barbup_api.infra.event.PasswordChangedEvent;
import com.barbup.barbup_api.infra.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.infra.email.EmailService;
import com.barbup.barbup_api.infra.email.EmailTemplateRenderer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;

@Slf4j
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
    @EventListener
    public void on(PasswordChangedEvent event) {

        try {
            String htmlBody = renderer.render("email/password_changed", Map.of(
                    "username", event.firstName(),
                    "changedAt", Instant.now()
            ));

            emailService.sendMail(event.email(), SUBJECT, htmlBody);
        } catch (Exception e) {
            log.error("Error while send email. {}", e.getMessage());
        }
    }
}
