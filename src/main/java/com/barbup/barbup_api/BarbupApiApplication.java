package com.barbup.barbup_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class BarbupApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarbupApiApplication.class, args);
	}

}
