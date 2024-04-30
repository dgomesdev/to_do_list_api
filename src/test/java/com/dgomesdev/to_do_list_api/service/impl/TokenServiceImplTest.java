package com.dgomesdev.to_do_list_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private HttpServletRequest request;

    private Field secretField;

    @BeforeEach
    void setUp() throws Exception {
        secretField = TokenServiceImpl.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, "mySecretKey");
    }

    @Test
    @DisplayName("Should generate token successfully")
    void givenValidAttributes_whenGeneratingToken_thenReturnValidToken() {
        // GIVEN
        String username = "username";

        // WHEN
        String token = tokenService.generateToken(username);

        // THEN
        assertNotNull(token);
        System.out.println(token);
    }

    @Test
    @DisplayName("Should throw exception when token secret is null")
    void givenInvalidSecret_whenGeneratingToken_thenThrowException() throws IllegalAccessException {
        // GIVEN
        String username = "username";
        secretField.set(tokenService, null);

        // WHEN
        var exception = assertThrows(JWTCreationException.class, () -> tokenService.generateToken(username));

        // THEN
        assertEquals("Error while generating token: The Secret cannot be null", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should throw exception when username is null")
    void givenInvalidUsername_whenGeneratingToken_thenThrowException() {
        // GIVEN
        // WHEN
        var exception = assertThrows(JWTCreationException.class, () -> tokenService.generateToken(null));

        // THEN
        assertEquals("Error while generating token: Invalid username", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should throw exception when username is blank")
    void givenBlankUsername_whenGeneratingToken_thenThrowException() {
        // GIVEN
        // WHEN
        var exception = assertThrows(JWTCreationException.class, () -> tokenService.generateToken(""));

        // THEN
        assertEquals("Error while generating token: Invalid username", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should validate token successfully")
    void givenValidAttributes_whenValidatingToken_thenReturnValidToken() {
        // GIVEN
        String validToken = JWT
                .create()
                .withIssuer("to_do_list_api")
                .withSubject("username")
                .withExpiresAt(Instant.now().plus(360, ChronoUnit.SECONDS))
                .sign(Algorithm.HMAC256("mySecretKey"));
        when(request.getHeader("Authorization")).thenReturn(validToken);

        // WHEN
        String validatedToken = tokenService.validateToken(request);

        // THEN
        assertEquals("username", validatedToken);
        System.out.println(validatedToken);
    }

    @Test
    @DisplayName("Should throw exception for expired token")
    void givenInvalidExpirationDate_whenValidatingToken_thenThrowException() {
        // GIVEN
        String expiredToken = JWT
                .create()
                .withIssuer("to_do_list_api")
                .withSubject("username")
                .withExpiresAt(Instant.now().minusSeconds(3600))
                .sign(Algorithm.HMAC256("mySecretKey"));
        when(request.getHeader("Authorization")).thenReturn(expiredToken);

        // WHEN
        var exception = assertThrows(JWTVerificationException.class, () -> tokenService.validateToken(request));

        // THEN
        assertTrue(exception.getLocalizedMessage().contains("Error while validating token: The Token has expired"));
        System.out.println(exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid token")
    void givenInvalidToken_whenValidatingToken_thenThrowException() {
        // GIVEN
        when(request.getHeader("Authorization")).thenReturn("");

        // WHEN
        var exception = assertThrows(JWTVerificationException.class, () -> tokenService.validateToken(request));

        // THEN
        assertTrue(exception.getLocalizedMessage().contains("Error while validating token"));
        System.out.println(exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should throw exception for null token")
    void givenNullToken_whenValidatingToken_thenThrowException() {
        // GIVEN
        when(request.getHeader("Authorization")).thenReturn(null);

        // WHEN
        var exception = assertThrows(JWTVerificationException.class, () -> tokenService.validateToken(request));

        // THEN
        assertEquals("Error while validating token: The token is null.", exception.getLocalizedMessage());
    }

}