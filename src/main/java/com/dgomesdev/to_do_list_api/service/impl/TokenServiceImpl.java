package com.dgomesdev.to_do_list_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dgomesdev.to_do_list_api.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api:security:token:secret}")
    private String secret;

    private Algorithm buildAlgorithm(String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Override
    public String generateToken(String username) {
        try {
            if (username == null || username.isBlank())
                throw new JWTCreationException(
                        "Invalid username",
                        new RuntimeException()
                );
            return JWT
                    .create()
                    .withIssuer("to_do_list_api")
                    .withSubject(username)
                    .withExpiresAt(getTokenExpirationDate())
                    .sign(buildAlgorithm(secret));
        } catch (Exception e) {
            throw new JWTCreationException(
                    "Error while generating token: " + e.getLocalizedMessage(),
                    new RuntimeException()
            );
        }
    }

    @Override
    public String validateToken(HttpServletRequest request) {
        try {
            var token = recoverToken(request);
            return JWT
                    .require(buildAlgorithm(secret))
                    .withIssuer("to_do_list_api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            throw new JWTVerificationException(
                    "Error while validating token: " + e.getLocalizedMessage(),
                    new RuntimeException()
            );
        }
    }

    private String recoverToken(HttpServletRequest request) throws IOException {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) throw new IOException("The token is null.");
        return authHeader.replace("Bearer ", "");
    }

    @Override
    public Instant getTokenExpirationDate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}