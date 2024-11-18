package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.TaskRepository;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskEntity mockTaskEntity;

    @Mock
    private UserEntity mockUserEntity;

    @Mock
    private TaskModel mockTaskModel;

    private final UUID userId = UUID.randomUUID();
    private final UUID taskId = UUID.randomUUID();
    private final Authentication authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            Set.of(UserAuthority.USER)
                    .stream()
                    .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
                    .toList()
    );

    @BeforeEach
    void setup() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should save task successfully")
    void givenValidTask_whenSavingTask_ThenReturnSavedTask() {
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(mockTaskEntity);

        //WHEN
        TaskModel response = taskService.saveTask(mockTaskModel);

        //THEN
        assertEquals(mockTaskModel.getTitle(), response.getTitle());
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when trying to save an invalid task")
    void givenInvalidTask_whenSavingTask_thenThrowException() {
        //GIVEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        //WHEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.saveTask(null));

        //THEN
        assertEquals("Invalid task: Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getTitle()\" because \"task\" is null", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(0)).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should find a task by ID successfully")
    void givenValidTaskId_whenFindTaskById_theReturnTask() {
        //GIVEN
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTaskEntity));
        when(mockTaskEntity.getUser()).thenReturn(mockUserEntity);
        when(mockUserEntity.getId()).thenReturn(userId);

        //WHEN
        TaskModel response = taskService.findTaskById(taskId);

        //THEN
        assertEquals(mockTaskModel.getTitle(), response.getTitle());
        verify(taskRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw an exception when trying to find by Id a non-existent task")
    void givenInvalidTaskId_whenFindingTaskById_thenThrowException() {
        //GIVEN
        TaskNotFoundException exception;

        //WHEN
        exception = assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(null));

        //THEN
        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Should update task successfully")
    void givenValidTask_whenUpdatingTask_ThenReturnUpdatedTask() {
        //GIVEN
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTaskEntity));
        when(mockTaskEntity.getUser()).thenReturn(mockUserEntity);
        when(mockUserEntity.getId()).thenReturn(userId);
        when(taskRepository.save(any())).thenReturn(mockTaskEntity);

        //WHEN
        TaskModel response = taskService.updateTask(taskId, mockTaskModel);

        //THEN
        assertEquals(mockTaskModel.getTitle(), response.getTitle());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when trying to update a task with invalid id")
    void givenInvalidTaskId_whenUpdatingTask_thenThrowException() {
        //GIVEN
        TaskNotFoundException exception;

        //WHEN
        exception = assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(null, mockTaskModel));

        //THEN
        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Should throw an exception when trying to update an invalid task")
    void givenInvalidTask_whenUpdatingTask_thenThrowException() {
        //GIVEN
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTaskEntity));
        when(mockTaskEntity.getUser()).thenReturn(mockUserEntity);
        when(mockUserEntity.getId()).thenReturn(userId);
        NullPointerException exception;

        //WHEN
        exception = assertThrows(NullPointerException.class, () -> taskService.updateTask(taskId, null));

        //THEN
        assertEquals("Cannot invoke \"com.dgomesdev.to_do_list_api.domain.model.TaskModel.getTitle()\" because \"task\" is null", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void givenValidTask_whenDeletingTask_thenDeleteTaskSuccessfully() {
        //GIVEN
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTaskEntity));
        when(mockTaskEntity.getUser()).thenReturn(mockUserEntity);
        when(mockUserEntity.getId()).thenReturn(userId);

        //WHEN
        taskService.deleteTask(taskId);

        //THEN
        assertDoesNotThrow(UnauthorizedUserException::new);
        assertDoesNotThrow(TaskNotFoundException::new);
        verify(taskRepository, times(1)).delete(mockTaskEntity);
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete a non-existent task")
    void givenInvalidTaskId_whenDeletingTask_thenThrowException() {
        //GIVEN
        TaskNotFoundException exception;

        //WHEN
        exception = assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        //THEN
        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete a task as an unauthorized user")
    void givenUnauthorizedUser_whenDeletingTask_thenThrowException() {
        //GIVEN
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTaskEntity));
        when(mockTaskEntity.getUser()).thenReturn(mockUserEntity);
        when(mockUserEntity.getId()).thenReturn(UUID.randomUUID());
        UnauthorizedUserException exception;

        //WHEN
        exception = assertThrows(UnauthorizedUserException.class, () -> taskService.deleteTask(taskId));

        //THEN
        assertEquals("Unauthorized user", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(0)).delete(any());
    }
}