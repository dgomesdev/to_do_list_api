package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.dto.response.TaskResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
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

    private final UserModel userModelRequest = new UserModel("username", "password", Set.of(UserAuthority.USER));
    private final UserEntity userEntity = new UserEntity(userModelRequest);

    private final UUID taskId = UUID.randomUUID();
    private final TaskRequestDto taskRequestDto = new TaskRequestDto(
            "Task title",
            "Task description",
            null,
            null
    );
    private final TaskModel taskModelRequest = new TaskModel(taskRequestDto);
    private final TaskModel taskModelResponse = new TaskModel(new TaskEntity(taskModelRequest, userEntity));
    private final TaskResponseDto taskResponseDto = new TaskResponseDto(taskModelResponse);

    @Test
    @DisplayName("Should save task successfully")
    void givenValidTask_whenSavingTask_thenReturnCreated() {
        //GIVE
        when(taskService.saveTask(any(TaskModel.class))).thenReturn(taskModelResponse);

        //WHEN
        ResponseEntity<?> response = taskController.saveTask(taskRequestDto);

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        TaskResponseDto responseBody = (TaskResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(taskResponseDto, response.getBody());
    }

    @Test
    @DisplayName("Should throw exception when the user tries to save an invalid task")
    void givenInvalidTask_whenSavingTask_thenThrowException() {
        //GIVEN
        var invalidTask = new TaskRequestDto(null, null, null, null);

        //WHEN
        Exception exception = assertThrows(NullPointerException.class, () -> taskController.saveTask(invalidTask));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getTaskId()\" because \"task\" is null", exception.getMessage());

    }

    @Test
    @DisplayName("Should find a task by Id successfully")
    void givenTaskId_whenFindingTaskById_thenReturnResponseOk() {
        //GIVEN
        when(taskService.findTaskById(taskId)).thenReturn(taskModelResponse);

        //WHEN
        ResponseEntity<?> response = taskController.findTaskById(taskId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TaskResponseDto responseBody = (TaskResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(taskResponseDto, responseBody);
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
        when(taskService.updateTask(eq(taskId), any(TaskModel.class))).thenReturn(taskModelResponse);

        //WHEN
        ResponseEntity<?> response = taskController.updateTask(taskId, taskRequestDto);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        TaskResponseDto responseBody = (TaskResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(taskResponseDto, responseBody);
    }

    @Test
    @DisplayName("Should throw an exception when the user tries to update a task with null id")
    void givenNullId_whenUpdatingTask_theThrowException() {
        //GIVEN
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () ->taskController.updateTask(null, taskRequestDto));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getTaskId()\" because \"task\" is null", exception.getMessage());
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

        //WHEN
        response = taskController.deleteTask(taskId);

        //THEN
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        MessageDto responseBody = (MessageDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Task deleted successfully", responseBody.message());
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