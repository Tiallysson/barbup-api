package com.barbup.barbup_api.infra.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Resend resend;

    public EmailService(Resend resend) {
        this.resend = resend;
    }

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
            throw new RuntimeException(e);
        }

    }
}
