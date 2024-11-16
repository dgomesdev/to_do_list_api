package com.dgomesdev.to_do_list_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api:security:token:secret}")
    private String secret;

    private Algorithm buildAlgorithm(String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Override
    public String generateToken(UserModel user) {
        try {
            return JWT
                    .create()
                    .withIssuer("to_do_list_api")
                    .withClaim("userId", user.getUserID().toString())
                    .withClaim("username", user.getUsername())
                    .withClaim("userAuthorities", user.getAuthorities().stream().map(Object::toString).toList())
                    .withExpiresAt(getTokenExpirationDate())
                    .sign(buildAlgorithm(secret));
        } catch (Exception e) {
            throw new JWTCreationException(
                    "Error while generating token: " + e.getMessage(),
                    new RuntimeException()
            );
        }
    }

    @Override
    public UserModel getUserFromToken(String token) {
        try {
            var decodedToken = validateToken(token);
            UUID userId = UUID.fromString(decodedToken.getClaim("userId").asString());
            String username = decodedToken.getClaim("username").asString();
            var authoritiesList = decodedToken.getClaim("userAuthorities").asList(String.class);
            Set<UserAuthority> userAuthorities = authoritiesList.stream()
                    .map(UserAuthority::valueOf)
                    .collect(Collectors.toSet());

            return new UserModel(userId, username, userAuthorities);
        } catch (Exception e) {
            throw new JWTVerificationException(
                    "Error while getting user from token: " + e.getMessage(),
                    new RuntimeException()
            );
        }
    }

    private DecodedJWT validateToken(String token) {
        try {
            return JWT
                    .require(buildAlgorithm(secret))
                    .withIssuer("to_do_list_api")
                    .build()
                    .verify(token);
        } catch (Exception e) {
            throw new JWTVerificationException(
                    "Error while validating token: " + e.getMessage(),
                    new RuntimeException()
            );
        }
    }

    private Instant getTokenExpirationDate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}