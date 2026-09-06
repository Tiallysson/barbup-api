package com.barbup.barbup_api.infra.event.listener;

import com.barbup.barbup_api.infra.email.EmailService;
import com.barbup.barbup_api.infra.email.EmailTemplateRenderer;
import com.barbup.barbup_api.infra.event.BarbershopCreatedEvent;
import com.barbup.barbup_api.infra.event.PasswordChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class BarbershopCreatedNotificationListener {
    private static final String SUBJECT = "Bem vindo - Barbup";

    private final EmailService emailService;
    private final EmailTemplateRenderer renderer;

    public BarbershopCreatedNotificationListener(EmailService emailService, EmailTemplateRenderer renderer) {
        this.emailService = emailService;
        this.renderer = renderer;
    }

    @Async
    @EventListener
    public void on(BarbershopCreatedEvent event) {

        try {

            String htmlBody = renderer.render("email/barbershop_welcome", Map.of(
                    "ownerName", event.ownerName(),
                    "barbershopName", event.barbershopName(),
                    "createdAt", event.createdAt()
            ));

            emailService.sendMail(event.email(), SUBJECT, htmlBody);
        } catch (Exception e) {
            log.error("Error while send email. {}", e.getMessage());
        }
    }
}
