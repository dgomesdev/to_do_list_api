package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import com.dgomesdev.to_do_list_api.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final User validUser = new User(
            UUID.randomUUID(),
            "dgomesdev",
            "email@email.com",
            "123456",
            UserRole.USER
    );

    @Test
    @DisplayName("Should save user successfully")
    void givenValidUser_whenSavingUser_ThenReturnSavedUser() {
        //GIVEN
        when(userRepository.save(any())).thenReturn(validUser);

        //WHEN
        userService.saveUser(validUser);

        //THEN
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid user")
    void givenInvalidUser_whenSavingUser_thenThrowException() {
        //GIVEN
        User user = new User();

        //WHEN
        Exception exception = assertThrows(Exception.class, () -> userService.saveUser(user));

        // THEN
        assertTrue(exception.getLocalizedMessage().contains("Invalid user"));
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an already existent user")
    void givenAlreadyExistentUser_whenSavingUser_thenThrowException() {
        // GIVEN
        when(userRepository.existsByUsername(validUser.getUsername())).thenReturn(true);

        //WHEN
        Exception exception = assertThrows(Exception.class, () -> userService.saveUser(validUser));

        // THEN
        assertEquals("Invalid user: User already exists", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should find an user by Id successfully")
    void givenValidUserId_whenFindUserByUserId_theReturnUser() {
        //GIVEN
        final User user = validUser;
        final UUID validUserId = user.getId();
        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));

        //WHEN
        var foundUser = userService.findUserById(validUserId);

        //THEN
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository, times(1)).findById(validUserId);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find by Id a non-existent user")
    void givenInvalidUserId_whenFindingUserByUserId_thenThrowException() {
        //GIVEN
        final UUID fakeUserId = UUID.randomUUID();
        when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        //WHEN
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.findUserById(fakeUserId));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should find an user by username successfully")
    void givenValidUserId_whenFindUserByUsername_theReturnUser() {
        //GIVEN
        final User user = validUser;
        final String validUsername = user.getUsername();
        when(userRepository.findUserByUsername(validUsername)).thenReturn(Optional.of(user));

        //WHEN
        var foundUser = userService.findUserByUsername(validUsername);

        //THEN
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository, times(1)).findUserByUsername(validUsername);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find by username a non-existent user")
    void givenInvalidUserId_whenFindingUserByUsername_thenThrowException() {
        //GIVEN
        final String invalidUsername = "";
        when(userRepository.findUserByUsername(invalidUsername)).thenReturn(Optional.empty());

        //WHEN
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername(invalidUsername));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should update user successfully")
    void givenValidUser_whenUpdatingUser_ThenReturnUpdatedUser() {
        //GIVEN
        when(userRepository.save(any())).thenReturn(validUser);

        //WHEN
        userService.updateUser(validUser);

        //THEN
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an user")
    void givenInvalidUser_whenUpdatingUser_thenThrowException() {
        //GIVEN
        final User user = new User();

        //WHEN
        Exception exception = assertThrows(Exception.class, () -> userService.updateUser(user));

        // THEN
        assertTrue(exception.getLocalizedMessage().contains("Invalid user"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void givenValidUserId_whenDeletingUser_thenDeleteUserSuccessfully() {
        //GIVEN
        final User userToBeDeleted = validUser;
        final UUID fakeUserId = userToBeDeleted.getId();
        when(userRepository.findById(fakeUserId)).thenReturn(Optional.of(userToBeDeleted));

        //WHEN
        userService.deleteUser(fakeUserId);

        //THEN
        verify(userRepository, times(1)).delete(userToBeDeleted);
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete an user")
    void givenInvalidUserId_whenDeletingUser_thenThrowException() {
        //GIVEN
        final UUID invalidUserId = UUID.randomUUID();
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        //WHEN
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(invalidUserId));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should find the user details of an user successfully")
    void givenValidUserId_whenFindUserByUsername_theReturnUserDetails() {
        //GIVEN
        final User user = validUser;
        final String validUsername = user.getUsername();
        when(userRepository.findUserByUsername(validUsername)).thenReturn(Optional.of(user));

        //WHEN
        var foundUser = userService.loadUserByUsername(validUsername);

        //THEN
        assertNotNull(foundUser);
        verify(userRepository, times(1)).findUserByUsername(validUsername);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find the user details of a non-existent user")
    void givenInvalidUserId_whenFindingUserDetailsByUsername_thenThrowException() {
        //GIVEN
        final String invalidUsername = "";
        when(userRepository.findUserByUsername(invalidUsername)).thenReturn(Optional.empty());

        //WHEN
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(invalidUsername));

        //THEN
        assertEquals("Username not found", exception.getLocalizedMessage());
    }
}