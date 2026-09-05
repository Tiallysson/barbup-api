package com.barbup.barbup_api.listener;

import com.barbup.barbup_api.event.PasswordResetRequestedEvent;
import com.barbup.barbup_api.infra.services.EmailService;
import com.barbup.barbup_api.infra.services.EmailTemplateRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PasswordResetRequestedEvent event) {

        String htmlBody = renderer.render("email/reset_password", Map.of(
                "username", event.firstName(),
                "verificationCode", event.code(),
                "expirationMinutes", event.ttl().toMinutes()
        ));

        emailService.sendMail(event.email(), SUBJECT, htmlBody);
    }
}
