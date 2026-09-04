package com.barbup.barbup_api.infra.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableConfigurationProperties(MailjetProperties.class)
public class MailjetConfig {

    @Bean
    public MailjetClient mailjetClient(MailjetProperties props) {
        return new MailjetClient(
                ClientOptions.builder()
                        .apiKey(props.apiKey())
                        .apiSecretKey(props.apiSecret())
                        .build()
        );
    }
}
