package com.dgomesdev.to_do_list_api.service.interfaces;

import com.dgomesdev.to_do_list_api.domain.model.TaskModel;

import java.util.UUID;

public interface TaskService {

    TaskModel saveTask(TaskModel newTask);
    TaskModel findTaskById(UUID taskId);
    TaskModel updateTask(UUID taskId, TaskModel updatedTask);
    UUID deleteTask(UUID taskId);
}