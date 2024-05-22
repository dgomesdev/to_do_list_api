package com.dgomesdev.to_do_list_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api:security:token:secret}")
    private String secret;

    private Algorithm buildAlgorithm(String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Override
    public String generateToken(UserDetails user, UUID userId) {
        try {
            if (user.getUsername() == null || user.getUsername().isBlank())
                throw new JWTCreationException("Invalid user", new RuntimeException());
            return JWT
                    .create()
                    .withIssuer("to_do_list_api")
                    .withJWTId(userId.toString())
                    .withSubject(user.getUsername())
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
    public Pair<String, String> validateToken(HttpServletRequest request) {
        try {
            var token = recoverToken(request);
            var jwt = JWT
                    .require(buildAlgorithm(secret))
                    .withIssuer("to_do_list_api")
                    .build()
                    .verify(token);
            return Pair.of(jwt.getSubject(), jwt.getId());
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