package com.barbup.barbup_api.infra.event.listener;

import com.barbup.barbup_api.infra.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.infra.email.EmailService;
import com.barbup.barbup_api.infra.email.EmailTemplateRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Component
public class PasswordResetNotificationListener {
    private static final String SUBJECT = "Recuperação de senha - Barbup";

    private final EmailService emailService;
    private final EmailTemplateRenderer renderer;

    public PasswordResetNotificationListener(EmailService emailService, EmailTemplateRenderer renderer) {
        this.emailService = emailService;
        this.renderer = renderer;
    }

    @Async
    @EventListener
    public void on(PasswordResetRequestedEvent event) {
        try {
            String htmlBody = renderer.render("email/reset_password", Map.of(
                    "username", event.firstName(),
                    "verificationCode", event.code(),
                    "expirationMinutes", event.ttl().toMinutes()
            ));

            emailService.sendMail(event.email(), SUBJECT, htmlBody);
        } catch (Exception e) {
            log.error("Error while send email. {}", e.getMessage());
        }
    }
}
