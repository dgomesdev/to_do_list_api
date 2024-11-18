package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final UUID userId = UUID.randomUUID();
    private final UserRequestDto userRequestDto = new UserRequestDto("username", "password");
    private final UserModel userModelRequest = new UserModel("username", "password", Set.of(UserAuthority.USER));
    private final UserModel userModelResponse = new UserModel(new UserEntity(userModelRequest));
    private final UserResponseDto userResponseDto = new UserResponseDto(userModelResponse);

    @Test
    @DisplayName("Should find user by Id successfully")
    void givenUserId_whenFindingUserById_thenReturnResponseOk() {
        //GIVEN
        when(userService.findUserById(userId)).thenReturn(userModelResponse);

        //WHEN
        ResponseEntity<?> response = userController.findUserById(userId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto responseBody = (UserResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(userResponseDto, responseBody);
    }

    @Test
    @DisplayName("Should throw an exception when the user passes a null id")
    void givenNullId_whenFindingUserById_thenThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () -> userController.findUserById(null));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.UserModel.getUserId()\" because \"user\" is null", exception.getMessage());
    }

    @Test
    @DisplayName("Should update user successfully")
    void givenUser_whenUpdatingUser_thenReturnResponseOk() {
        //GIVEN
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        UserModel userModelUpdate = new UserModel(userRequestDto.username(), encodedPassword, Set.of(UserAuthority.USER));
        when(userService.updateUser(userId, userModelUpdate)).thenReturn(userModelResponse);

        //WHEN
        ResponseEntity<?> response = userController.updateUser(userId, userRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto responseBody = (UserResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(userResponseDto, responseBody);
    }

    @Test
    @DisplayName("Should throw an exception when trying to update with a null id")
    void givenNullId_whenUpdatingUser_thenThrowException() {
        //GIVEN
        IllegalArgumentException exception;

        //WHEN
        exception = assertThrows(IllegalArgumentException.class, () -> userController.updateUser(null, userRequestDto));

        //THEN
        assertEquals("Cannot pass null or empty values to constructor", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete user successfully")
     void givenUserId_whenDeletingUser_thenReturnResponseNoContent() {
        //GIVEN
        ResponseEntity<?> response;

        //WHEN
        response = userController.deleteUser(userId);

        //THEN
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        MessageDto responseBody = (MessageDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User deleted successfully", responseBody.message());
    }

    @Test
    @DisplayName("Should throw an exception when user passes null id")
     void givenNullId_whenDeletingUser_thenThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () -> userController.deleteUser(null));

        //THEN
        assertEquals("userId cannot be null", exception.getMessage());

    }
}