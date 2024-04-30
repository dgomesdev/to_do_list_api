package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import com.dgomesdev.to_do_list_api.domain.model.Task;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final UUID mockUserId = UUID.randomUUID();

    private final Task validTask = new Task(
            UUID.randomUUID(),
            "task title",
            "task description",
            Priority.LOW,
            Status.TO_BE_DONE,
            mockUserId
    );

    @Test
    @DisplayName("Should save task successfully")
    void givenValidTask_whenSavingTask_ThenReturnSavedTask() {
        //GIVEN
        when(taskRepository.save(any())).thenReturn(validTask);

        //WHEN
        taskService.saveTask(validTask);

        //THEN
        verify(taskRepository, times(1)).save(validTask);
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid task")
    void givenInvalidUser_whenSavingUser_thenThrowException() {
        //GIVEN
        final Task newTask = new Task();
        when(taskRepository.save(any())).thenThrow(IllegalArgumentException.class);

        //WHEN
        var exception = assertThrows(RuntimeException.class, () -> taskService.saveTask(newTask));

        //THEN
        assertTrue(exception.getLocalizedMessage().contains("Invalid task"));
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    @DisplayName("Should find a task by ID successfully")
    void givenValidUserId_whenFindUserByUserId_theReturnUser() {
        //GIVEN
        when(taskRepository.findById(any())).thenReturn(Optional.of(validTask));

        //WHEN
        var foundTask = taskService.findTaskById(validTask.getId());

        //THEN
        assertEquals(validTask, foundTask);
        verify(taskRepository, times(1)).findById(validTask.getId());
    }

    @Test
    @DisplayName("Should throw an exception when trying to find by Id a non-existent task")
    void givenInvalidUserId_whenFindingUserByUserId_thenThrowException() {
        //GIVEN
        var fakeTaskId = UUID.randomUUID();
        when(taskRepository.findById(any())).thenReturn(Optional.empty());

        //WHEN
        var exception = assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(fakeTaskId));

        //THEN
        assertEquals("Task not found", exception.getLocalizedMessage());
        verify(taskRepository, times(1)).findById(fakeTaskId);
    }
    @Test
    @DisplayName("Should find all tasks successfully")
    void givenValidUserId_whenFindingAllTasksByUserId_ThenReturnListOfAllTasks() {
        //GIVEN
        final List<Task> taskList = List.of(validTask);
        when(userService.findUserById(mockUserId)).thenReturn(new User());
        when(taskRepository.findTasksByUserId(mockUserId)).thenReturn(taskList);

        //WHEN
        var foundTasks = taskService.findAllTasksByUserId(mockUserId);

        //THEN
        assertThat(foundTasks.get(0).getUserId()).isEqualTo(mockUserId);
        verify(taskRepository, times(1)).findTasksByUserId(mockUserId);
    }

    @Test
    @DisplayName("Should return an empty list of tasks when user doesn't exist")
    void givenInvalidUserId_whenFindingAllTasksByUserId_ThenReturnAnEmptyList() {
        //GIVEN
        final var fakeUserId = UUID.randomUUID();
        when(userService.findUserById(fakeUserId)).thenThrow(new UserNotFoundException());

        //WHEN
        var exception = assertThrows(RuntimeException.class, () -> taskService.findAllTasksByUserId(fakeUserId));

        //THEN
        assertEquals("User not found", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Should update task successfully")
    void givenValidUser_whenUpdatingUser_ThenReturnUpdatedUser() {
        //GIVEN
        when(taskRepository.save(any())).thenReturn(validTask);

        //WHEN
        taskService.updateTask(validTask);

        //THEN
        verify(taskRepository, times(1)).save(validTask);

    }

    @Test
    @DisplayName("Should throw an exception when trying to update a task")
    void givenInvalidUser_whenUpdatingUser_thenThrowException() {
        //GIVEN
        final Task newTask = new Task();
        when(taskRepository.save(any())).thenThrow(IllegalArgumentException.class);

        //WHEN
        var exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(newTask));

        //THEN
        assertTrue(exception.getLocalizedMessage().contains("Invalid task"));
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void givenValidTask_whenDeletingUser_thenDeleteUserSuccessfully() {
        //GIVEN
        //WHEN
        taskService.deleteTask(validTask);

        //THEN
        verify(taskRepository, times(1)).delete(validTask);
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete a task")
    void givenError_whenDeletingUser_thenThrowException() {
        //GIVEN
        doThrow(new RuntimeException("Task not found")).when(taskRepository).delete(any());

        //WHEN
        var exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(validTask));

        //THEN
        assertEquals("Error: Task not found", exception.getLocalizedMessage());
        verify(taskRepository, times(1)).delete(validTask);
    }
}