package com.dgomesdev.to_do_list_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret}")
    protected String secret;

    private Algorithm buildAlgorithm(String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Override
    public String generateToken(UserModel user) {
        Instant expirationHour = setExpirationHour();
        return JWT
                .create()
                .withIssuer("to_do_list_api")
                .withClaim("userId", user.getUserId().toString())
                .withClaim("userAuthorities", user.getAuthorities().stream().map(GrantedAuthority::toString).toList())
                .withExpiresAt(expirationHour)
                .sign(buildAlgorithm(secret));
    }

    @Override
    public UserModel getUserFromToken(String token) {
        var decodedToken = validateToken(token);

        UUID userId = UUID.fromString(decodedToken.getClaim("userId").asString());
        var authoritiesList = decodedToken.getClaim("userAuthorities").asList(String.class);
        Set<UserAuthority> userAuthorities = authoritiesList.stream()
                .map(UserAuthority::valueOf)
                .collect(Collectors.toSet());

        return new UserModel.Builder()
                .withUserId(userId)
                .withUserAuthorities(userAuthorities)
                .withUsername("user")
                .build();
    }

    private DecodedJWT validateToken(String token) {
        return JWT
                .require(buildAlgorithm(secret))
                .withIssuer("to_do_list_api")
                .build()
                .verify(token);
    }

    private Instant setExpirationHour() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}