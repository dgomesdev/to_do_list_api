package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenServiceImpl tokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserModel mockUserModel = new UserModel(
            UUID.randomUUID(),
            "dgomesdev",
            "email@email.com",
            "123456",
            UserRole.USER
    );

    private final UserEntity mockUserEntity = new UserEntity(mockUserModel);

    final UserModel invalidUser = new UserModel(mockUserModel.id(), null, null, null, null);

    @Test
    @DisplayName("Should save user successfully")
    void givenValidUser_whenSavingUser_ThenReturnSavedUser() {
        //GIVEN
        when(userRepository.existsByUsername(mockUserModel.username())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(mockUserEntity);

        //WHEN
        userService.saveUser(mockUserModel);

        //THEN
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an already existent user")
    void givenAlreadyExistentUser_whenSavingUser_thenThrowException() {
        // GIVEN
        when(userRepository.existsByUsername(mockUserModel.username())).thenReturn(true);

        //WHEN
        final Exception exception = assertThrows(Exception.class, () -> userService.saveUser(mockUserModel));

        // THEN
        assertEquals("Invalid user: User already exists", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid user")
    void givenInvalidUser_whenSavingUser_thenThrowException() {
        //GIVEN
        //WHEN
        final Exception exception = assertThrows(Exception.class, () -> userService.saveUser(invalidUser));

        // THEN
        assertTrue(exception.getLocalizedMessage().contains("Invalid user"));
        System.out.println(exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should find an user by Id successfully")
    void givenValidUserId_whenFindUserByUserId_theReturnUser() {
        //GIVEN
        final UUID validUserId = mockUserModel.id();
        when(userRepository.findById(validUserId)).thenReturn(Optional.of(mockUserEntity));

        //WHEN
        final var foundUser = userService.findUserById(validUserId);

        //THEN
        assertEquals(mockUserModel, foundUser);
        verify(userRepository, times(1)).findById(validUserId);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find by Id a non-existent user")
    void givenInvalidUserId_whenFindingUserByUserId_thenThrowException() {
        //GIVEN
        final UUID fakeUserId = UUID.randomUUID();
        when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        //WHEN
        final Exception exception = assertThrows(UserNotFoundException.class, () -> userService.findUserById(fakeUserId));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should find an user by username successfully")
    void givenValidUserId_whenFindUserByUsername_theReturnUser() {
        //GIVEN
        final String validUsername = mockUserModel.username();
        when(userRepository.findUserByUsername(validUsername)).thenReturn(Optional.of(mockUserEntity));

        //WHEN
        final var foundUser = userService.findUserByUsername(validUsername);

        //THEN
        assertEquals(mockUserModel, foundUser);
        verify(userRepository, times(1)).findUserByUsername(validUsername);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find by username a non-existent user")
    void givenInvalidUserId_whenFindingUserByUsername_thenThrowException() {
        //GIVEN
        final String invalidUsername = "";
        when(userRepository.findUserByUsername(invalidUsername)).thenReturn(Optional.empty());

        //WHEN
        final Exception exception = assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername(invalidUsername));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should update user successfully")
    void givenValidUser_whenUpdatingUser_ThenReturnUpdatedUser() {
        //GIVEN
        when(userRepository.findById(mockUserModel.id())).thenReturn(Optional.of(mockUserEntity));
        when(userRepository.save(any())).thenReturn(mockUserEntity);
        when(tokenService.generateToken(any())).thenReturn("Valid token");

        //WHEN
        final var token = userService.updateUser(mockUserModel);

        //THEN
        verify(userRepository, times(1)).save(any());
        assertEquals("Valid token", token);
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an user")
    void givenInvalidUser_whenUpdatingUser_thenThrowException() {
        //GIVEN
        when(userRepository.findById(mockUserModel.id())).thenReturn(Optional.of(mockUserEntity));

        //WHEN
        final Exception exception = assertThrows(Exception.class, () -> userService.updateUser(invalidUser));

        // THEN
        assertTrue(exception.getLocalizedMessage().contains("Invalid user"));
        System.out.println(exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void givenValidUserId_whenDeletingUser_thenDeleteUserSuccessfully() {
        //GIVEN
        when(userRepository.findById(mockUserEntity.getId())).thenReturn(Optional.of(mockUserEntity));

        //WHEN
        userService.deleteUser(mockUserEntity.getId());

        //THEN
        verify(userRepository, times(1)).delete(mockUserEntity);
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete an user")
    void givenInvalidUserId_whenDeletingUser_thenThrowException() {
        //GIVEN
        when(userRepository.findById(mockUserEntity.getId())).thenReturn(Optional.empty());

        //WHEN
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(mockUserModel.id()));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should find the user details of an user successfully")
    void givenValidUserId_whenFindUserByUsername_theReturnUserDetails() {
        //GIVEN
        final String validUsername = mockUserModel.username();
        when(userRepository.findUserByUsername(validUsername)).thenReturn(Optional.of(new UserEntity(mockUserModel)));

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
        assertEquals("Username  not found", exception.getLocalizedMessage());
    }
}