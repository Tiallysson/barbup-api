package com.barbup.barbup_api.infra.services;

public interface EmailService {
    String sendMail(String to, String subject, String htmlBody);
}
