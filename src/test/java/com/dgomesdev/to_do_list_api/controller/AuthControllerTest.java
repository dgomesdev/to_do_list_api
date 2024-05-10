package com.dgomesdev.to_do_list_api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.dgomesdev.to_do_list_api.controller.dto.request.AuthRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.domain.exception.UserAlreadyExistsException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    private final UserModel mockUserModel = new UserModel(UUID.randomUUID(), userRequestDto, UserRole.USER);

    private final String validToken = JWT
            .create()
            .withIssuer("to_do_list_api")
            .withSubject(username)
            .withExpiresAt(Instant.now().plus(360, ChronoUnit.SECONDS))
            .sign(Algorithm.HMAC256("mySecretKey"));

    private UserDetails buildUserDetails() {
        return new User(username, password, true, true, true, true, List.of());
    }

    UserDetails authPrincipal = buildUserDetails();

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authPrincipal, null, authPrincipal.getAuthorities());


    @Test
    @DisplayName("Should register user successfully")
    public void givenNewUser_whenRegisteringUser_returnResponseCreated() {
        //GIVEN
        doNothing().when(userService).saveUser(any());

        //WHEN
        var responseCreated = authController.register(userRequestDto);

        //THEN
        assertEquals(HttpStatus.CREATED, responseCreated.getStatusCode());
        assertNotNull(responseCreated.getBody());
        var message = responseCreated.getBody().toString();
        assertEquals("MessageDto[message=User registered successfully]", message);
    }

    @Test
    @DisplayName("Should return a bad request response when trying to register an invalid user")
    public void givenInvalidUser_whenRegisteringUser_returnResponseBadRequest() {
        //GIVEN
        doThrow(new IllegalArgumentException()).when(userService).saveUser(any());

        //WHEN
        var responseBadRequest = authController.register(userRequestDto);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseBadRequest.getStatusCode());
        assertNotNull(responseBadRequest.getBody());
        var message = responseBadRequest.getBody().toString();
        assertEquals("MessageDto[message=Invalid user]", message);
    }

    @Test
    @DisplayName("Should return an internal server error response when trying to register an already existent user")
    public void givenAlreadyExistentUser_whenRegisteringUser_returnResponseInternalServerError() {
        //GIVEN
        doThrow(new UserAlreadyExistsException()).when(userService).saveUser(any());

        //WHEN
        var responseInternalServerError = authController.register(userRequestDto);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseInternalServerError.getStatusCode());
        assertNotNull(responseInternalServerError.getBody());
        var message = responseInternalServerError.getBody().toString();
        assertTrue(message.contains("Error while creating user"));
        System.out.println(responseInternalServerError.getBody());
    }

    @Test
    @DisplayName("Should log in user successfully")
    public void givenValidAttributes_whenLoggingInUser_thenReturnResponseOk() {
        //GIVEN
        when(userService.findUserByUsername(authRequestDto.username())).thenReturn(mockUserModel);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateToken(authPrincipal)).thenReturn(validToken);
        // UsernamePasswordAuthenticationToken [Principal=dgomesdev, Credentials=[PROTECTED], Authenticated=false, Details=null, Granted Authorities=[]]
        // UsernamePasswordAuthenticationToken [Principal=org.springframework.security.core.userdetails.User [Username=dgomesdev, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, CredentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[USER]], Credentials=[PROTECTED], Authenticated=true, Details=null, Granted Authorities=[USER]]
        // org.springframework.security.core.userdetails.User [Username=dgomesdev, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, CredentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[USER]]

        //WHEN
        var responseOk = authController.login(authRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, responseOk.getStatusCode());
        assertNotNull(responseOk.getBody());
        var message = responseOk.getBody().toString();
        assertTrue(message.contains("Login successful for user " + username));
        System.out.println(responseOk.getBody());
    }

    @Test
    @DisplayName("Should return an unauthorized response when trying to register a non-existent user")
    public void givenNonExistentUser_whenRegisteringUser_returnResponseUnauthorized() {
        //GIVEN
        when(userService.findUserByUsername(username)).thenThrow(new UserNotFoundException());

        //WHEN
        var responseUnauthorized = authController.login(authRequestDto);

        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
        assertNotNull(responseUnauthorized.getBody());
        var message = responseUnauthorized.getBody().toString();
        assertEquals("MessageDto[message=User not found]", message);
    }

    @Test
    @DisplayName("Should return an internal server error response when trying to logging in user with invalid credentials")
    public void givenInvalidAttributes_whenLoggingInUser_returnResponseInternalServerError() {
        //GIVEN
        when(userService.findUserByUsername(authRequestDto.username())).thenReturn(mockUserModel);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateToken(authPrincipal)).thenThrow(JWTCreationException.class);

        //WHEN
        var responseInternalServerError = authController.login(authRequestDto);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseInternalServerError.getStatusCode());
        assertNotNull(responseInternalServerError.getBody());
        var message = responseInternalServerError.getBody().toString();
        assertTrue(message.contains("Error while logging in"));
        System.out.println(responseInternalServerError.getBody());
    }
}
