package com.dgomesdev.to_do_list_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private UserModel mockUserModel;

    private final UUID userId = UUID.randomUUID();

    Set<UserAuthority> userAuthorities = Set.of(UserAuthority.USER);

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
        when(mockUserModel.getUserId()).thenReturn(userId);
        when(mockUserModel.getUsername()).thenReturn("username");
        when(mockUserModel.getAuthorities()).thenReturn(userAuthorities
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toSet())
        );

        // WHEN
        String token = tokenService.generateToken(mockUserModel);

        // THEN
        assertNotNull(token);
    }

    @Test
    @DisplayName("Should throw exception when token secret is null")
    void givenInvalidSecret_whenGeneratingToken_thenThrowException() throws IllegalAccessException {
        // GIVEN
        secretField.set(tokenService, null);
        when(mockUserModel.getUserId()).thenReturn(userId);
        when(mockUserModel.getUsername()).thenReturn("username");
        when(mockUserModel.getAuthorities()).thenReturn(userAuthorities
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toSet())
        );

        // WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tokenService.generateToken(mockUserModel));

        // THEN
        assertEquals("The Secret cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should get user from token successfully")
    void givenValidToken_whenGettingUserFromToken_thenReturnValidUser() {
        // GIVEN
        when(mockUserModel.getUserId()).thenReturn(userId);
        when(mockUserModel.getUsername()).thenReturn("username");
        when(mockUserModel.getAuthorities()).thenReturn(userAuthorities
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toSet())
        );
        String token = tokenService.generateToken(mockUserModel);

        // WHEN
        UserModel validUser = tokenService.getUserFromToken(token);

        // THEN
        assertEquals(UserModel.class, validUser.getClass());
    }

    @Test
    @DisplayName("Should throw exception for expired token")
    void givenInvalidExpirationDate_whenValidatingToken_thenThrowException() {
        // GIVEN
        String expiredToken = JWT
                .create()
                .withIssuer("to_do_list_api")
                .withClaim("userId", userId.toString())
                .withClaim("username", "username")
                .withClaim("userAuthorities", List.of(UserAuthority.USER.name()))
                .withExpiresAt(Instant.now().minusSeconds(3600))
                .sign(Algorithm.HMAC256("mySecretKey"));

        // WHEN
        var exception = assertThrows(TokenExpiredException.class, () -> tokenService.getUserFromToken(expiredToken));

        // THEN
        assertTrue(exception.getMessage().contains("The Token has expired"));
    }

    @Test
    @DisplayName("Should throw exception for invalid token")
    void givenInvalidToken_whenValidatingToken_thenThrowException() {
        // GIVEN
        JWTDecodeException exception;

        // WHEN
        exception = assertThrows(JWTDecodeException.class, () -> tokenService.getUserFromToken(""));

        // THEN
        assertEquals("The token was expected to have 3 parts, but got 0.", exception.getMessage());
    }
}