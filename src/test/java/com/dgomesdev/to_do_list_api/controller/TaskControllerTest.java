package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.*;
import com.dgomesdev.to_do_list_api.service.impl.TaskServiceImpl;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskServiceImpl taskService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private TaskController taskController;

    private HttpServletRequest mockRequest;
    private UUID mockUserId;
    private UUID mockTaskId;
    private UserModel mockUserModel;
    private TaskRequestDto mockTaskRequestDto;
    private TaskModel mockTaskModel;

    @BeforeEach
    void setup() {
        mockUserId = UUID.randomUUID();
        mockTaskId = UUID.randomUUID();
        mockRequest = mock(HttpServletRequest.class);
        mockTaskRequestDto = new TaskRequestDto(
                "TaskTitle",
                "TaskDescription",
                Priority.LOW,
                Status.TO_BE_DONE
        );
        mockUserModel = new UserModel(
                mockUserId,
                "username",
                "email",
                "password",
                UserRole.USER
        );
        mockTaskModel = new TaskModel(mockTaskId, mockTaskRequestDto, mockUserId);
    }

    @Test
    @DisplayName("Should save task successfully")
    void givenValidTaskRequestDto_whenSavingTask_thenReturnCreated() {
        //GIVE
        when(userService.findUserById(any())).thenReturn(mockUserModel);
        doNothing().when(taskService).saveTask(any());

        //WHEN
        var responseOk = taskController.saveTask(mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseOk.getBody());
        assertEquals(HttpStatus.CREATED, responseOk.getStatusCode());
//        assertEquals("Task created successfully", responseOk.getBody());
//        verify(taskService, times(1)).saveTask(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when the user is not authorized to save a task")
    void givenUnauthorizedUser_whenSavingTask_thenReturnUnauthorized() {
        //GIVEN
        when(userService.findUserById(any())).thenThrow(new UserNotFoundException());

        //WHEN
        var responseUnauthorized = taskController.saveTask(mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseUnauthorized.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
//        assertEquals("Unauthorized user", responseUnauthorized.getBody());
//        verify(taskService, times(0)).saveTask(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid task")
    void givenInvalidTask_whenSavingTask_thenReturnResponseError() {
        //GIVEN
        when(userService.findUserById(any())).thenReturn(mockUserModel);
        doThrow(new IllegalArgumentException()).when(taskService).saveTask(any());

        //WHEN
        var responseError= taskController.saveTask(mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseError.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
//        assertTrue(responseError.getBody().contains("Error while saving the task"));
//        verify(taskService, times(1)).saveTask(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should find a task by Id successfully")
    void givenTaskId_whenFindingTaskById_thenReturnResponseOk() {
        //GIVEN
        when(taskService.findTaskById(any())).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);

        //WHEN
        var responseOk = taskController.findTaskById(mockTaskId, mockRequest);

        //THEN
        assertNotNull(responseOk.getBody());
        assertEquals(HttpStatus.OK, responseOk.getStatusCode());
        verify(taskService, times(1)).findTaskById(mockTaskId);
    }

    @Test
    @DisplayName("Should throw exception when the user is not authorized to find a task")
    void givenUnauthorizedUser_whenFindingTAskById_thenReturnResponseUnauthorized() {
        //GIVEN
        when(taskService.findTaskById(any())).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());

        //WHEN
        var responseUnauthorized = taskController.findTaskById(UUID.randomUUID(), mockRequest);

        //THEN
        assertNotNull(responseUnauthorized.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
//        assertEquals("Unauthorized user", responseUnauthorized.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when trying to find a task that does not exist")
    void givenANonExistentTask_whenFindingTaskById_thenReturnResponseNotFound() {
        //GIVEN
        when(taskService.findTaskById(any())).thenThrow(new TaskNotFoundException());

        //WHEN
        var responseNotFound = taskController.findTaskById(UUID.randomUUID(), mockRequest);

        //THEN
        assertNotNull(responseNotFound.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
//        assertEquals("Task not found", responseNotFound.getBody());
        verify(taskService, times(1)).findTaskById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw an exception when trying to find a task and an error occurs")
    void givenTask_whenFindingTaskById_thenReturnResponseError() {
        //GIVEN
        when(taskService.findTaskById(any())).thenThrow(new RuntimeException());

        //WHEN
        var responseError = taskController.findTaskById(UUID.randomUUID(), mockRequest);

        //THEN
        assertNotNull(responseError.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
        assertTrue(responseError.getBody().toString().contains("Error while saving task"));
        verify(taskService, times(1)).findTaskById(any(UUID.class));
    }

    @Test
    @DisplayName("Should return a list with all the tasks of a valid user")
    void givenValidUserId_whenFindingAllTaskByUserId_thenReturnResponseOk() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        when(userService.findUserById(mockUserId)).thenReturn(mockUserModel);
        when(taskService.findAllTasksByUserId(any())).thenReturn(List.of(mockTaskModel));

        //WHEN
        var responseOk = taskController.findAllTasks(mockRequest);

        //THEN
        assertNotNull(responseOk.getBody());
        assertEquals(HttpStatus.OK, responseOk.getStatusCode());
        verify(taskService, times(1)).findAllTasksByUserId(mockUserId);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find tha tasks of an unauthorized user")
    void givenUnauthorizedUser_whenFindingAllTasksByUserId_thenReturn_ResponseUnauthorized() {
        // GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(userService.findUserById(any())).thenThrow(new UserNotFoundException());

        // WHEN
        var responseUnauthorized = taskController.findAllTasks(mockRequest);

        // THEN
        assertNotNull(responseUnauthorized.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
//        assertEquals("Unauthorized user", responseUnauthorized.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an error occurs")
    void givenUserId_whenFindingAllTasksByUserId_thenReturnResponseError() {
        //GIVEN
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(userService.findUserById(any())).thenReturn(mockUserModel);
        when(taskService.findAllTasksByUserId(mockUserId)).thenThrow(new RuntimeException());

        //WHEN
        var responseError= taskController.findAllTasks(mockRequest);

        //THEN
        assertNotNull(responseError.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
        assertTrue(responseError.getBody().toString().contains("Error while listing the tasks"));
    }

    @Test
    @DisplayName("Should update task successfully")
    void givenValidTask_whenUpdatingTask_theReturnResponseOk() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        doNothing().when(taskService).updateTask(any(), any());

        //WHEN
        var responseOk = taskController.updateTask(mockTaskId, mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseOk);
        assertEquals(HttpStatus.OK, responseOk.getStatusCode());
//        assertEquals("Task updated successfully", responseOk.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an unauthorized user tries to update a task")
    void givenUnauthorizedUser_whenUpdatingTask_thenReturnResponseUnauthorized() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());

        //WHEN
        var responseUnauthorized = taskController.updateTask(mockTaskId, mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseUnauthorized.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
//        assertEquals("Unauthorized user", responseUnauthorized.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when the user tries to update a non existent task")
    void givenNonExistentTask_whenUpdatingTask_theReturnResponseNotFound() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenThrow(new TaskNotFoundException());

        //WHEN
        var responseNotFound = taskController.updateTask(mockTaskId, mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseNotFound.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
//        assertEquals("Task not found", responseNotFound.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an error occurs")
    void errorWhileUpdatingTask() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        doThrow(RuntimeException.class).when(taskService).updateTask(any(), any());

        //WHEN
        var responseError = taskController.updateTask(mockTaskId, mockTaskRequestDto, mockRequest);

        //THEN
        assertNotNull(responseError.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
//        assertTrue(responseError.getBody().contains("Error while updating the task"));
    }

    @Test
    @DisplayName("Should delete a task successfully")
    void givenTaskId_whenDeletingTask_thenReturnResponseNoContent() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);

        //WHEN
        var responseNoContent = taskController.deleteTask(mockTaskId, mockRequest);

        //THEN
        assertNotNull(responseNoContent.getBody());
        assertEquals(HttpStatus.NO_CONTENT, responseNoContent.getStatusCode());
//        assertEquals("Task deleted successfully", responseNoContent.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an unauthorized user tries to delete a task")
    void givenUnauthorizedUser_whenDeletingTask_thenReturnResponseUnauthorized() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(UUID.randomUUID());

        //WHEN
        var responseUnauthorized = taskController.deleteTask(mockTaskId, mockRequest);

        //THEN
        assertNotNull(responseUnauthorized.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, responseUnauthorized.getStatusCode());
//        assertEquals("Unauthorized user", responseUnauthorized.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when the user tries to delete a non existent task")
    void givenNonExistentTaskId_whenDeletingTask_thenReturnResponseNotFound() {
        //GIVEN
        when(taskService.findTaskById(any())).thenThrow(new TaskNotFoundException());

        //WHEN
        var responseNotFound = taskController.deleteTask(mockUserId, mockRequest);

        //THEN
        assertNotNull(responseNotFound.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
//        assertEquals("Task not found", responseNotFound.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when an error occurs")
    void givenTaskId_whenDeletingTask_thenReturnResponseError() {
        //GIVEN
        when(taskService.findTaskById(mockTaskId)).thenReturn(mockTaskModel);
        when(mockRequest.getAttribute("userId")).thenReturn(mockUserId);
        doThrow(RuntimeException.class).when(taskService).deleteTask(any());

        //WHEN
        var responseError = taskController.deleteTask(mockTaskId, mockRequest);

        //THEN
        assertNotNull(responseError.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseError.getStatusCode());
//        assertTrue(responseError.getBody().contains("Error while deleting the task"));
    }
}