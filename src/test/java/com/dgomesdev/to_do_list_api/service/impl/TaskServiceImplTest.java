//package com.dgomesdev.to_do_list_api.service.impl;
//
//import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
//import com.dgomesdev.to_do_list_api.data.repository.TaskRepository;
//import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
//import com.dgomesdev.to_do_list_api.domain.model.Priority;
//import com.dgomesdev.to_do_list_api.domain.model.Status;
//import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TaskServiceImplTest {
//
//    @Mock
//    private TaskRepository taskRepository;
//
//    @InjectMocks
//    private TaskServiceImpl taskService;
//
//    private final UUID mockUserId = UUID.randomUUID();
//
//    private final TaskModel mockTaskModel = new TaskModel(
//            UUID.randomUUID(),
//            "task title",
//            "task description",
//            Priority.LOW,
//            Status.TO_BE_DONE,
//            mockUserId
//    );
//
//    private final TaskEntity mockTaskEntity = new TaskEntity(mockTaskModel);
//
//    private final TaskModel invalidTask = new TaskModel(mockTaskModel.id(), null, null, null, null, UUID.randomUUID());
//
//    @Test
//    @DisplayName("Should save task successfully")
//    void givenValidTask_whenSavingTask_ThenReturnSavedTask() {
//        //GIVEN
//        when(taskRepository.save(any())).thenReturn(mockTaskEntity);
//
//        //WHEN
//        taskService.saveTask(mockTaskModel);
//
//        //THEN
//        verify(taskRepository, times(1)).save(any());
//    }
//
//    @Test
//    @DisplayName("Should throw an exception when trying to save an invalid task")
//    void givenInvalidUser_whenSavingUser_thenThrowException() {
//        //GIVEN
//        when(taskRepository.save(any())).thenThrow(IllegalArgumentException.class);
//
//        //WHEN
//        var exception = assertThrows(RuntimeException.class, () -> taskService.saveTask(invalidTask));
//
//        //THEN
//        assertTrue(exception.getLocalizedMessage().contains("Invalid task"));
//        verify(taskRepository, times(1)).save(any());
//    }
//
//    @Test
//    @DisplayName("Should find a task by ID successfully")
//    void givenValidUserId_whenFindUserByUserId_theReturnUser() {
//        //GIVEN
//        when(taskRepository.findById(mockTaskEntity.getId())).thenReturn(Optional.of(mockTaskEntity));
//
//        //WHEN
//        var foundTask = taskService.findTaskById(mockTaskEntity.getId());
//
//        //THEN
//        assertEquals(mockTaskModel, foundTask);
//        verify(taskRepository, times(1)).findById(mockTaskEntity.getId());
//    }
//
//    @Test
//    @DisplayName("Should throw an exception when trying to find by Id a non-existent task")
//    void givenInvalidUserId_whenFindingUserByUserId_thenThrowException() {
//        //GIVEN
//        when(taskRepository.findById(any())).thenReturn(Optional.empty());
//
//        //WHEN
//        var exception = assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(invalidTask.id()));
//
//        //THEN
//        assertEquals("Task not found", exception.getLocalizedMessage());
//        verify(taskRepository, times(1)).findById(any());
//    }
//    @Test
//    @DisplayName("Should find all tasks successfully")
//    void givenValidUserId_whenFindingAllTasksByUserId_ThenReturnListOfAllTasks() {
//        //GIVEN
//        final List<TaskEntity> taskEntityList = List.of(mockTaskEntity);
//        when(taskRepository.findTasksByUserId(mockUserId)).thenReturn(taskEntityList);
//
//        //WHEN
//        var foundTasks = taskService.findAllTasksByUserId(mockUserId);
//
//        //THEN
//        assertEquals(mockUserId, foundTasks.get(0).userId());
//        verify(taskRepository, times(1)).findTasksByUserId(mockUserId);
//    }
//
//    @Test
//    @DisplayName("Should return an empty list of tasks when user doesn't exist")
//    void givenInvalidUserId_whenFindingAllTasksByUserId_ThenReturnAnEmptyList() {
//        //GIVEN
//        when(taskRepository.findTasksByUserId(any())).thenThrow(new RuntimeException("Error"));
//
//        //WHEN
//        var exception = assertThrows(RuntimeException.class, () -> taskService.findAllTasksByUserId(invalidTask.userId()));
//
//        //THEN
//        assertEquals("Error", exception.getLocalizedMessage());
//        System.out.println(exception.getLocalizedMessage());
//    }
//
//    @Test
//    @DisplayName("Should update task successfully")
//    void givenValidUser_whenUpdatingUser_ThenReturnUpdatedUser() {
//        //GIVEN
//        when(taskRepository.findById(mockTaskEntity.getId())).thenReturn(Optional.of(mockTaskEntity));
//        when(taskRepository.save(any())).thenReturn(mockTaskEntity);
//
//        //WHEN
//        taskService.updateTask(mockTaskModel);
//
//        //THEN
//        verify(taskRepository, times(1)).save(mockTaskEntity);
//
//    }
//
//    @Test
//    @DisplayName("Should throw an exception when trying to update a task")
//    void givenInvalidUser_whenUpdatingUser_thenThrowException() {
//        //GIVEN
//        when(taskRepository.findById(mockTaskEntity.getId())).thenReturn(Optional.of(mockTaskEntity));
//        when(taskRepository.save(any())).thenThrow(IllegalArgumentException.class);
//
//        //WHEN
//        var exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(mockTaskModel));
//
//        //THEN
//        assertTrue(exception.getLocalizedMessage().contains("Invalid task"));
//        verify(taskRepository, times(1)).save(any());
//    }
//
//    @Test
//    @DisplayName("Should delete task successfully")
//    void givenValidTask_whenDeletingUser_thenDeleteUserSuccessfully() {
//        //GIVEN
//        //WHEN
//        taskService.deleteTask(mockTaskModel);
//
//        //THEN
//        verify(taskRepository, times(1)).delete(any());
//    }
//
//    @Test
//    @DisplayName("Should throw an exception when trying to delete a task")
//    void givenError_whenDeletingUser_thenThrowException() {
//        //GIVEN
//        doThrow(new RuntimeException("Error")).when(taskRepository).delete(any());
//
//        //WHEN
//        var exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(mockTaskModel));
//
//        //THEN
//        assertEquals("Error: Error", exception.getLocalizedMessage());
//        verify(taskRepository, times(1)).delete(any());
//    }
//}