package com.barbup.barbup_api.infra.email;

public interface EmailService {
    String sendMail(String to, String subject, String htmlBody);
}
