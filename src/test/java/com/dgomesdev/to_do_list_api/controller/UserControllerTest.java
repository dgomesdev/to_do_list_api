package com.dgomesdev.to_do_list_api.controller;

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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserRequestDto mockUserRequestDto;

    @Mock
    private UserModel mockUserModel;

    @Mock
    private UserResponseDto mockUserResponseDto;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Should find user by Id successfully")
    void givenUserId_whenFindingUserById_thenReturnResponseOk() {
        //GIVEN
        when(userService.findUserById(userId)).thenReturn(mockUserModel);

        //WHEN
        ResponseEntity<?> response = userController.findUserById(userId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto responseBody = (UserResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(mockUserResponseDto.userId(), responseBody.userId());
        assertEquals(mockUserResponseDto.username(), responseBody.username());
        assertEquals(mockUserResponseDto.tasks(), responseBody.tasks());
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
        when(mockUserRequestDto.username()).thenReturn("username");
        when(mockUserRequestDto.password()).thenReturn("");
        when(mockUserRequestDto.email()).thenReturn("");
        when(userService.updateUser(any(UserModel.class))).thenReturn(mockUserModel);

        //WHEN
        ResponseEntity<?> response = userController.updateUser(userId, mockUserRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto responseBody = (UserResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(mockUserResponseDto.userId(), responseBody.userId());
        assertEquals(mockUserResponseDto.username(), responseBody.username());
        assertEquals(mockUserResponseDto.tasks(), responseBody.tasks());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update with a null id")
    void givenNullId_whenUpdatingUser_thenThrowException() {
        //GIVEN
        IllegalArgumentException exception;
        when(mockUserRequestDto.email()).thenReturn("");

        //WHEN
        exception = assertThrows(IllegalArgumentException.class, () -> userController.updateUser(null, mockUserRequestDto));

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