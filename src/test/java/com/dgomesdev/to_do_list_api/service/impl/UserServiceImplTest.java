package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserModel mockUserModel;

    private final UUID userId = UUID.randomUUID();

    private final UserModel validUser = new UserModel.Builder()
            .withUsername("username")
            .withPassword("password")
            .withUserAuthorities(Set.of(UserAuthority.USER))
            .withEmail("danilo.gomes@dgomesdev.com")
            .build();

    UserEntity validUserEntity = new UserEntity(
            "username",
            "password",
            "danilo.gomes@dgomesdev.com",
            Collections.emptySet()
    );

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, tokenService);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(UserAuthority.toGrantedAuthority(UserAuthority.USER))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should save user successfully")
    void givenValidUser_whenSavingUser_ThenReturnSavedUser() {
        //GIVEN
        when(userRepository.save(any(UserEntity.class))).thenReturn(validUserEntity);

        //WHEN
        UserModel response = userService.saveUser(validUser);

        //THEN
        assertEquals("username", response.getUsername());
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid password")
    void givenInvalidPassword_whenSavingUser_thenThrowException() {
        // GIVEN
        when(mockUserModel.getEmail()).thenReturn("danilo.gomes@dgomesdev.com");
        when(mockUserModel.getPassword()).thenReturn("");

        //WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(mockUserModel));

        // THEN
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid password")
    void givenInvalidEmail_whenSavingUser_thenThrowException() {
        // GIVEN
        when(mockUserModel.getEmail()).thenReturn("email");

        //WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(mockUserModel));

        // THEN
        assertEquals("Invalid e-mail", exception.getMessage());
    }

    @Test
    @DisplayName("Should find an user by Id successfully")
    void givenValidUserId_whenFindUserByUserId_theReturnUser() {
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(validUserEntity));

        //WHEN
        UserModel response = userService.findUserById(userId);

        //THEN
        assertEquals("username", response.getUsername());
    }

    @Test
    @DisplayName("Should throw an exception when trying to find an invalid user")
    void givenInvalidUserId_whenFindingUserById_thenThrowException() {
        //GIVEN
        UnauthorizedUserException exception;

        //WHEN
        exception = assertThrows(UnauthorizedUserException.class, () -> userService.findUserById(UUID.randomUUID()));

        //THEN
        assertTrue(exception.getMessage().contains("Unauthorized access"));
    }

    @Test
    @DisplayName("Should update user successfully")
    void givenValidUser_whenUpdatingUser_ThenReturnUpdatedUser() {
        // GIVEN
        UserModel newUser = new UserModel.Builder()
                .withUserId(userId)
                .withUsername("new username")
                .withUserAuthorities(Set.of(UserAuthority.USER))
                .withPassword("password")
                .withEmail("newEmail@dgomesdev.com")
                .build();
        String encodedPassword = passwordEncoder.encode("password");
        UserEntity existingUser = new UserEntity("username", encodedPassword, "oldEmail@dgomesdev.com", Collections.emptySet());
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);

        // WHEN
        var response = userService.updateUser(newUser);

        // THEN
        assertEquals("new username", response.getUsername());
        assertEquals("newEmail@dgomesdev.com", existingUser.getEmail());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an user with invalid id")
    void givenUnauthorizedUser_whenUpdatingUser_thenThrowException() {
        // GIVEN
        when(mockUserModel.getUserId()).thenReturn(UUID.randomUUID());

        //WHEN
        UnauthorizedUserException exception = assertThrows(UnauthorizedUserException.class, () -> userService.updateUser(mockUserModel));

        // THEN
        assertTrue(exception.getMessage().contains("Unauthorized access"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void givenValidUserId_whenDeletingUser_thenDeleteUserSuccessfully() {
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(validUserEntity));

        //WHEN
        userService.deleteUser(userId);

        //THEN
        assertDoesNotThrow(() -> new UnauthorizedUserException(userId));
        assertDoesNotThrow(() -> new UserNotFoundException(userId));
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete a non-existent user")
    void givenInvalidUser_whenDeletingUser_thenThrowException() {
        //GIVEN
        UserNotFoundException exception;

        //WHEN
        exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        //THEN
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete an user with invalid Id")
    void givenInvalidId_whenDeletingUser_thenThrowException() {
        //GIVEN
        UnauthorizedUserException exception;

        //WHEN
        exception = assertThrows(UnauthorizedUserException.class, () -> userService.deleteUser(UUID.randomUUID()));

        //THEN
        assertTrue(exception.getMessage().contains("Unauthorized access"));
    }

    @Test
    @DisplayName("Should find an user by username successfully")
    void givenValidUsername_whenFindUserByUserId_theReturnUser() {
        //GIVEN
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.of(validUserEntity));

        //WHEN
        UserDetails response = userService.loadUserByUsername("email");

        //THEN
        assertEquals("username", response.getUsername());
    }

    @Test
    @DisplayName("Should throw an exception when trying to find an invalid username")
    void givenInvalidUsername_whenFindingUserById_thenThrowException() {
        //GIVEN
        UsernameNotFoundException exception;

        //WHEN
        exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(""));

        //THEN
        assertEquals("User not found with email: ", exception.getMessage());
    }
}