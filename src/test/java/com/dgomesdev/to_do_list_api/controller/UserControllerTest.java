package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import com.dgomesdev.to_do_list_api.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    HttpServletRequest mockRequest;

    @InjectMocks
    UserController userController;

    private UUID mockUserId;
    private UserRequestDto mockUserRequestDto;
    private User mockUser;

    @BeforeEach
    void setup() {
        mockUserId = UUID.randomUUID();
        mockRequest = mock(HttpServletRequest.class);
        mockRequest.setAttribute("userId", mockUserId);
        mockUserRequestDto = new UserRequestDto(
                "username",
                "email",
                "password"
        );
        mockUser = new User(
                mockUserId,
                "username",
                "email",
                "password",
                UserRole.USER
        );
        mockUser = new User(mockUserRequestDto);
    }

    @Test
    @DisplayName("Should find user by Id successfully")
    void givenUserId_whenFindingUserById_thenReturnResponseOk() {
        //GIVEN
        when(userService.findUserById(mockUserId)).thenReturn(mockUser);

        //WHEN
        var responseOk = userController.findUserById(mockUserId);

        //THEN
        assertEquals(HttpStatus.OK, responseOk.getStatusCode());
        assertNotNull(responseOk.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void givenNonExistentUser_whenFindingUserById_thenReturnResponseNotFound() {
        //GIVEN
        when(userService.findUserById(any())).thenThrow(new UserNotFoundException());

        //WHEN
        var responseNotFound = userController.findUserById(UUID.randomUUID());

        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
        assertEquals("User not found", responseNotFound.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an error occurs")
    void givenUserId_whenFindingUserById_thenReturnResponseError() {
        //GIVEN
        when(userService.findUserById(any())).thenThrow(new RuntimeException());

        //WHEN
        var responseError = userController.findUserById(mockUserId);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
        assertNotNull(responseError.getBody());
        assertTrue(responseError.getBody().toString().contains("Error while finding the user"));
    }

    @Test
    @DisplayName("Should update user successfully")
    void givenUser_whenUpdatingUser_thenReturnResponseOk() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        when(userService.findUserById(any())).thenReturn(mockUser);
        doNothing().when(userService).updateUser(any());

        //WHEN
        var responseOk = userController.updateUser(mockUserId, mockUserRequestDto, mockRequest);

        //THEN
        assertEquals(HttpStatus.OK, responseOk.getStatusCode());
        assertEquals("User updated successfully", responseOk.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an unauthorized user tries to update an user")
    void givenUnauthorizedUser_whenUpdatingUser_thenReturnResponseUnauthorized() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());

        //WHEN
        var responseUnauthorized = userController.updateUser(mockUserId, mockUserRequestDto, mockRequest);

        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
        assertEquals("Unauthorized user", responseUnauthorized.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update a non-existent user")
    void givenNonExistentUser_whenUpdatingUser_thenReturnResponseNotFound() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        when(userService.findUserById(any())).thenThrow(new UserNotFoundException());

        //WHEN
        var responseNotFound = userController.updateUser(mockUserId, mockUserRequestDto, mockRequest);

        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
        assertEquals("User not found", responseNotFound.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an invalid user")
    void givenNonInvalidUser_whenUpdatingUser_thenReturnResponseBadRequest() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        when(userService.findUserById(any())).thenReturn(mockUser);
        doThrow(new IllegalArgumentException()).when(userService).updateUser(any());

        //WHEN
        var responseBadRequest = userController.updateUser(mockUserId, mockUserRequestDto, mockRequest);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseBadRequest.getStatusCode());
        assertEquals("Invalid user", responseBadRequest.getBody());
    }
    @Test
    @DisplayName("Should throw an exception when an error occurs")
    void givenInvalidUser_whenUpdatingUser_thenReturnResponseError() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        when(userService.findUserById(any())).thenReturn(mockUser);
        doThrow(new RuntimeException()).when(userService).updateUser(any());

        //WHEN
        var responseError = userController.updateUser(mockUserId, mockUserRequestDto, mockRequest);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
        assertNotNull(responseError.getBody());
        assertTrue(responseError.getBody().contains("Error while updating the user"));
    }

    @Test
    @DisplayName("Should delete user successfully")
     void givenUserId_whenDeletingUser_thenReturnResponseNoContent() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        doNothing().when(userService).deleteUser(mockUserId);

        //WHEN
        var responseNoContent = userController.deleteUser(mockUserId, mockRequest);

        //THEN
        assertEquals(HttpStatus.NO_CONTENT, responseNoContent.getStatusCode());
        assertEquals("User deleted successfully", responseNoContent.getBody());
    }

    @Test
    @DisplayName("Should throw an unauthorized user tries to delete an user")
     void givenUnauthorizedUser_whenDeletingUser_thenReturnResponseUnauthorized() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());

        //WHEN
        var responseUnauthorized = userController.deleteUser(mockUserId, mockRequest);

        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
        assertEquals("Unauthorized user", responseUnauthorized.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete an non-existent user")
     void givenNonExistentUser_whenDeletingUser_thenReturnResponseNotFound() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        doThrow(new UserNotFoundException()).when(userService).deleteUser(mockUserId);

        //WHEN
        var responseNotFound = userController.deleteUser(mockUserId, mockRequest);

        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
        assertEquals("User not found", responseNotFound.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an error occurs")
     void givenUserId_whenDeletingUser_thenReturnResponseError() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        doThrow(new RuntimeException()).when(userService).deleteUser(mockUserId);

        //WHEN
        var responseError = userController.deleteUser(mockUserId, mockRequest);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
        assertNotNull(responseError.getBody());
        assertTrue(responseError.getBody().contains("Error while deleting the user"));
    }
}