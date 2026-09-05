package com.barbup.barbup_api.infra.services;

import com.barbup.barbup_api.shared.exception.EmailSendException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResendEmailService implements EmailService {
    private final Resend resend;

    @Override
    public String sendMail(String to, String subject, String htmlBody) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Barbup <onboarding@resend.dev>")
                .to(to)
                .subject(subject)
                .html(htmlBody)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            return response.getId();
        } catch (ResendException e) {
            throw new EmailSendException("Failed to send email through Resend", e);
        }
    }
}
