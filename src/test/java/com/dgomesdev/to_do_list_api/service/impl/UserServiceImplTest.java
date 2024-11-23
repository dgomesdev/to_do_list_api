package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserModel mockUserModel;

    @Mock
    private UserEntity mockUserEntity;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Set.of(UserAuthority.USER)
                        .stream()
                        .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
                        .toList()
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
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);
        when(mockUserEntity.getUsername()).thenReturn("username");
        when(mockUserEntity.getPassword()).thenReturn("password");
        when(mockUserEntity.getUserAuthorities()).thenReturn(Set.of(UserAuthority.USER));

        //WHEN
        UserModel response = userService.saveUser(mockUserModel);

        //THEN
        assertEquals("username", response.getUsername());
        assertEquals("password", response.getPassword());
        assertEquals(mockUserModel.getTasks(), response.getTasks());
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid user")
    void givenInvalidUser_whenSavingUser_thenThrowException() {
        // GIVEN
        RuntimeException exception;

        //WHEN
        exception = assertThrows(RuntimeException.class, () -> userService.saveUser(null));

        // THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.UserModel.getUsername()\" because \"user\" is null", exception.getMessage());
    }

    @Test
    @DisplayName("Should find an user by Id successfully")
    void givenValidUserId_whenFindUserByUserId_theReturnUser() {
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));
        when(mockUserEntity.getUsername()).thenReturn("username");
        when(mockUserEntity.getPassword()).thenReturn("password");
        when(mockUserEntity.getUserAuthorities()).thenReturn(Set.of(UserAuthority.USER));

        //WHEN
        UserModel response = userService.findUserById(userId);

        //THEN
        assertEquals("username", response.getUsername());
        assertEquals("password", response.getPassword());
        assertEquals(mockUserModel.getTasks(), response.getTasks());
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
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);
        when(mockUserEntity.getUsername()).thenReturn("username");
        when(mockUserEntity.getPassword()).thenReturn("password");
        when(mockUserEntity.getUserAuthorities()).thenReturn(Set.of(UserAuthority.USER));

        //WHEN
        UserModel response = userService.updateUser(userId, mockUserModel);

        //THEN
        assertEquals("username", response.getUsername());
        assertEquals("password", response.getPassword());
        assertEquals(mockUserModel.getTasks(), response.getTasks());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an invalid user")
    void givenInvalidUser_whenUpdatingUser_thenThrowException() {
        // GIVEN
        IllegalArgumentException exception;

        //WHEN
        exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, null));

        // THEN
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an user with invalid id")
    void givenUnauthorizedUser_whenUpdatingUser_thenThrowException() {
        // GIVEN
        UnauthorizedUserException exception;

        //WHEN
        exception = assertThrows(UnauthorizedUserException.class, () -> userService.updateUser(UUID.randomUUID(), mockUserModel));

        // THEN
        assertTrue(exception.getMessage().contains("Unauthorized access"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void givenValidUserId_whenDeletingUser_thenDeleteUserSuccessfully() {
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

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
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(mockUserEntity));
        when(mockUserEntity.getUsername()).thenReturn("username");
        when(mockUserEntity.getPassword()).thenReturn("password");
        when(mockUserEntity.getUserAuthorities()).thenReturn(Set.of(UserAuthority.USER));

        //WHEN
        var response = userService.loadUserByUsername("username");

        //THEN
        assertEquals("username", response.getUsername());
        assertEquals("password", response.getPassword());
    }

    @Test
    @DisplayName("Should throw an exception when trying to find an invalid username")
    void givenInvalidUsername_whenFindingUserById_thenThrowException() {
        //GIVEN
        UsernameNotFoundException exception;

        //WHEN
        exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(""));

        //THEN
        assertEquals("User not found with username: ", exception.getMessage());
    }
}