package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.Task;
import com.dgomesdev.to_do_list_api.domain.repository.TaskRepository;
import com.dgomesdev.to_do_list_api.service.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceImpl userService;

    public TaskServiceImpl(TaskRepository taskRepository, UserServiceImpl userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public void saveTask(Task task) {
        try {
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Invalid task: " + e.getLocalizedMessage());
        }
    }

    @Override
    public Task findTaskById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public List<Task> findAllTasksByUserId(UUID userId) {
        try {
            userService.findUserById(userId);
            return taskRepository.findTasksByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        try {
            taskRepository.save(updatedTask);
        } catch (Exception e) {
            throw new RuntimeException("Invalid task: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void deleteTask(Task task) {
        try {
            taskRepository.delete(task);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getLocalizedMessage());
        }
    }
}
