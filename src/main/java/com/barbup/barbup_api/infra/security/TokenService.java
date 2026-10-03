package com.barbup.barbup_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.barbup.barbup_api.domain.entity.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    private static final Duration TOKEN_TTL = Duration.ofHours(2);

    private final String secret;

    public TokenService(@Value("${api.security.token.secret}") String secret) {
        this.secret = secret;
    }

    public String generateToken(User user, Instant expiresAt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer("barbup-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while authenticating");
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("barbup-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public Instant generateExpirationDate() {
        return Instant.now().plus(TOKEN_TTL);
    }
}
