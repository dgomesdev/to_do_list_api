package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.TaskResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.TaskService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private TaskRequestDto mockTaskRequestDto;

    @Mock
    private TaskModel mockTaskModel;

    @Mock
    private TaskResponseDto mockTaskResponseDto;

    @Mock
    private UserModel mockUserModel;

    private final UUID taskId = UUID.randomUUID();

    @Test
    @DisplayName("Should save task successfully")
    void givenValidTask_whenSavingTask_thenReturnCreated() {
        //GIVE
        when(taskService.saveTask(any(TaskModel.class))).thenReturn(mockTaskModel);
        when(userService.findUserById(any())).thenReturn(mockUserModel);

        //WHEN
        ResponseEntity<?> response = taskController.saveTask(mockTaskRequestDto);

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should throw exception when the user tries to save an invalid task")
    void givenInvalidTask_whenSavingTask_thenThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () -> taskController.saveTask(mockTaskRequestDto));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getUserId()\" because \"savedTask\" is null", exception.getMessage());

    }

    @Test
    @DisplayName("Should find a task by Id successfully")
    void givenTaskId_whenFindingTaskById_thenReturnResponseOk() {
        //GIVEN
        when(taskService.findTaskById(taskId)).thenReturn(mockTaskModel);

        //WHEN
        ResponseEntity<?> response = taskController.findTaskById(taskId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when trying to find a task with a null id")
    void givenNullId_whenFindingTaskById_thenThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () ->taskController.findTaskById(null));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getTaskId()\" because \"task\" is null", exception.getMessage());
    }

    @Test
    @DisplayName("Should update task successfully")
    void givenValidTask_whenUpdatingTask_theReturnResponseOk() {
        //GIVEN
        when(taskService.updateTask(eq(taskId), any(TaskModel.class))).thenReturn(mockTaskModel);
        when(userService.findUserById(any())).thenReturn(mockUserModel);

        //WHEN
        ResponseEntity<?> response = taskController.updateTask(taskId, mockTaskRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when the user tries to update a task with null id")
    void givenNullId_whenUpdatingTask_theThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () ->taskController.updateTask(null, mockTaskRequestDto));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getUserId()\" because \"updatedTask\" is null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception when the user tries to update an invalid task")
    void givenInvalidTask_whenUpdatingTask_theThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () ->taskController.updateTask(taskId, null));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto.title()\" because \"taskRequestDto\" is null", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete a task successfully")
    void givenTaskId_whenDeletingTask_thenReturnResponseNoContent() {
        //GIVEN
        ResponseEntity<?> response;
        when(userService.findUserById(any())).thenReturn(mockUserModel);

        //WHEN
        response = taskController.deleteTask(taskId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when the user tries to delete a non existent task")
    void givenNonExistentTaskId_whenDeletingTask_thenReturnResponseNotFound() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () -> taskController.deleteTask(null));

        //THEN
        assertEquals("taskId cannot be null", exception.getMessage());
    }
}