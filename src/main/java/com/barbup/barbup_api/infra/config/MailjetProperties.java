package com.barbup.barbup_api.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mailjet")
public record MailjetProperties(
        String apiKey,
        String apiSecret,
        String senderEmail,
        String senderName
) {}
