package com.dgomesdev.to_do_list_api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.dgomesdev.to_do_list_api.controller.dto.request.AuthRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.domain.exception.UserAlreadyExistsException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import com.dgomesdev.to_do_list_api.service.impl.TokenServiceImpl;
import com.dgomesdev.to_do_list_api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private TokenServiceImpl tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    private final String username = "username";
    private final String password = "password";
    private final String email = "email";
    private final UserRequestDto userRequestDto = new UserRequestDto(username, email, password);
    private final AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

    private final String validToken = JWT
            .create()
            .withIssuer("to_do_list_api")
            .withSubject(username)
            .withExpiresAt(Instant.now().plus(360, ChronoUnit.SECONDS))
            .sign(Algorithm.HMAC256("mySecretKey"));

    private User mockUserRetrievalAndAuthentication() {
        User mockUser = new User(UUID.randomUUID(), username, email, password, UserRole.USER);
        when(userService.findUserByUsername(username)).thenReturn(mockUser);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))).thenReturn(
                new UsernamePasswordAuthenticationToken(mockUser, password, mockUser.getAuthorities()));
        return mockUser;
    }


    @Test
    @DisplayName("Should register user successfully")
    public void givenNewUser_whenRegisteringUser_returnOkResponse() {
        //GIVEN
        doNothing().when(userService).saveUser(any());

        //WHEN
        var response = authController.register(userRequestDto);

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getBody(), "User registered successfully");
    }

    @Test
    @DisplayName("Should return a bad request response when trying to register an invalid user")
    public void givenInvalidUser_whenRegisteringUser_returnBadRequestResponse() {
        //GIVEN
        doThrow(new IllegalArgumentException()).when(userService).saveUser(any());

        //WHEN
        var response = authController.register(userRequestDto);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(response.getBody(), "Invalid user");
    }

    @Test
    @DisplayName("Should return an internal server error response when trying to register an already existent user")
    public void givenAlreadyExistentUser_whenRegisteringUser_returnInternalServerErrorResponse() {
        //GIVEN
        doThrow(new UserAlreadyExistsException()).when(userService).saveUser(any());

        //WHEN
        var response = authController.register(userRequestDto);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error while creating user"));
        System.out.println(response.getBody());
    }

    @Test
    @DisplayName("Should log in user successfully")
    public void givenValidAttributes_whenLoggingInUser_thenReturnOkResponse() {
        //GIVEN
        var user = mockUserRetrievalAndAuthentication();
        when(tokenService.generateToken(user)).thenReturn(validToken);

        //WHEN
        var response = authController.login(authRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Login successful for user " + username));
        //verify(authenticationManager).authenticate(mockUsernamePassword);
        System.out.println(response.getBody());
    }

    @Test
    @DisplayName("Should return an unauthorized response when trying to register a non-existent user")
    public void givenNonExistentUser_whenRegisteringUser_returnUnauthorizedResponse() {
        //GIVEN
        when(userService.findUserByUsername(username)).thenThrow(new UserNotFoundException());

        //WHEN
        ResponseEntity<String> response = authController.login(authRequestDto);

        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(response.getBody(), "User not found");
    }

    @Test
    @DisplayName("Should return an internal server error response when trying to logging in user with invalid credentials")
    public void givenInvalidAttributes_whenLoggingInUser_returnBInternalServerErrorResponse() {
        //GIVEN
        var user = mockUserRetrievalAndAuthentication();
        when(tokenService.generateToken(user)).thenThrow(JWTCreationException.class);

        //WHEN
        ResponseEntity<String> response = authController.login(authRequestDto);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error while logging in"));
        System.out.println(response.getBody());
    }
}
