package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.RecoverPasswordService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private RecoverPasswordService recoverPasswordService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    Authentication mockAuthentication;

    @Mock
    private UserRequestDto mockUserRequestDto;

    @Mock
    private UserModel mockUserModel;

    @Test
    @DisplayName("Should register user successfully")
    public void givenNewUser_whenRegisteringUser_returnResponseCreated() {
        //GIVEN
        UserRequestDto userRequestDto = new UserRequestDto("username", "danilo.gomes@dgomesdev.com", "password");
        when(userService.saveUser(any(UserModel.class))).thenReturn(mockUserModel);
        doNothing().when(recoverPasswordService).sendMail(anyString(), anyString(), anyString());

        // WHEN
        ResponseEntity<?> response = authController.register(userRequestDto);

        // THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserResponseDto responseBody = (UserResponseDto) response.getBody();
        assertNotNull(responseBody);
    }

    @Test
    @DisplayName("Should throw an exception when trying to register an invalid user")
    public void givenInvalidUser_whenRegisteringUser_throwException() {
        //GIVEN
        IllegalArgumentException exception;

        //WHEN
        exception = assertThrows(IllegalArgumentException.class, () -> authController.register(mockUserRequestDto));

        //THEN
        assertEquals("Cannot pass null or empty values to constructor", exception.getMessage());
    }

    @Test
    @DisplayName("Should log in user successfully")
    public void givenValidAttributes_whenLoggingInUser_thenReturnResponseOk() {
        //GIVEN
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserModel);

        //WHEN
        ResponseEntity<?> response = authController.login(mockUserRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto responseBody = (UserResponseDto) response.getBody();
        assertNotNull(responseBody);
    }

    @Test
    @DisplayName("Should throw an exception when trying to log in an invalid user")
    public void givenInvalidUser_whenLoggingInUser_throwException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () -> authController.login(mockUserRequestDto));

        //THEN
        assertEquals("Cannot invoke \"org.springframework.security.core.Authentication.getPrincipal()\" because \"authentication\" is null", exception.getMessage());
    }
}
