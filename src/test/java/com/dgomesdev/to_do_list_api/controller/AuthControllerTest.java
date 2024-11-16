package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final UserRequestDto user = new UserRequestDto("username", "password");
    private final UserModel userModel = new UserModel("username", "password", Set.of(UserAuthority.USER));
    private final String token = "sample token";

    @Test
    @DisplayName("Should register user successfully")
    public void givenNewUser_whenRegisteringUser_returnResponseCreated() {
        //GIVEN
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(UserModel.class))).thenReturn(userModel);
        when(tokenService.generateToken(userModel)).thenReturn(token);

        //WHEN
        ResponseEntity<?> response = authController.register(user);

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        MessageDto responseBody = (MessageDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User registered successfully. Token: " + token, responseBody.message());
    }

    @Test
    @DisplayName("Should throw an exception when trying to register an invalid user")
    public void givenInvalidUser_whenRegisteringUser_throwException() {
        //GIVEN
        var invalidUser = new UserRequestDto("", "123");

        //WHEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authController.register(invalidUser));

        //THEN
        assertEquals("Cannot pass null or empty values to constructor", exception.getMessage());
    }

    @Test
    @DisplayName("Should log in user successfully")
    public void givenValidAttributes_whenLoggingInUser_thenReturnResponseOk() {
        //GIVEN
        Authentication authentication = new UsernamePasswordAuthenticationToken(userModel, userModel.getPassword());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(userModel)).thenReturn(token);

        //WHEN
        ResponseEntity<?> response = authController.login(user);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MessageDto responseBody = (MessageDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Login successful for user " + user.username() + " with token " + token , responseBody.message());
    }
}
