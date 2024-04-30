package com.dgomesdev.to_do_list_api.service;

import com.dgomesdev.to_do_list_api.domain.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    void saveTask(Task newTask);
    Task findTaskById(UUID taskId);
    List<Task> findAllTasksByUserId(UUID userId);
    void updateTask(Task updatedTask);
    void deleteTask(Task task);
}