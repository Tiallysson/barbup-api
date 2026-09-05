package com.barbup.barbup_api.infra.services;

import com.barbup.barbup_api.shared.exception.EmailSendException;
import com.barbup.barbup_api.infra.config.MailjetProperties;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.EmailResult;
import com.mailjet.client.transactional.response.MessageResult;
import com.mailjet.client.transactional.response.SendEmailError;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import com.mailjet.client.transactional.response.SentMessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class MailjetEmailService implements EmailService {
    private final MailjetClient mailjetClient;
    private final MailjetProperties properties;

    @Override
    public String sendMail(String to, String subject, String htmlBody) {
        TransactionalEmail message = TransactionalEmail.builder()
                .from(new SendContact(properties.senderEmail(), properties.senderName()))
                .to(new SendContact(to))
                .subject(subject)
                .htmlPart(htmlBody)
                .build();

        try {
            SendEmailsResponse response = SendEmailsRequest.builder()
                    .message(message)
                    .build()
                    .sendWith(mailjetClient);

            return extractMessageId(response);
        } catch (MailjetException e) {
            throw new EmailSendException("Failed to send email through Mailjet", e);
        }
    }

    private String extractMessageId(SendEmailsResponse response) {
        MessageResult[] messages = response.getMessages();

        if (messages == null || messages.length == 0)
            throw new EmailSendException("Mailjet returned no message result");

        MessageResult result = messages[0];

        if (result.getStatus() != SentMessageStatus.SUCCESS)
            throw new EmailSendException("Mailjet rejected the email: " + describeErrors(result));

        List<EmailResult> recipients = result.getTo();

        if (recipients == null || recipients.isEmpty())
            throw new EmailSendException("Mailjet returned no recipient result");

        return recipients.get(0).getMessageUUID();
    }

    private String describeErrors(MessageResult result) {
        SendEmailError[] errors = result.getErrors();

        if (errors == null || errors.length == 0)
            return "no error detail returned";

        return Arrays.stream(errors)
                .map(SendEmailError::getErrorMessage)
                .collect(Collectors.joining("; "));
    }
}
