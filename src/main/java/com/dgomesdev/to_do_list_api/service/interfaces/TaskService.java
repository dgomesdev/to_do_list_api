package com.dgomesdev.to_do_list_api.service.interfaces;

import com.dgomesdev.to_do_list_api.domain.model.TaskModel;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    void saveTask(TaskModel taskModel);
    TaskModel findTaskById(UUID taskId);
    List<TaskModel> findAllTasksByUserId(UUID userId);
    void updateTask(TaskModel taskModelToBeUpdated, TaskModel updatedTaskModel);
    void deleteTask(TaskModel taskModel);
}