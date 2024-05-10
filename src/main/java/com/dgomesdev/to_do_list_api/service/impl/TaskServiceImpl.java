package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.data.repository.TaskRepository;
import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.service.interfaces.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void saveTask(TaskModel taskModel) {
        try {
            taskRepository.save(new TaskEntity(taskModel));
        } catch (Exception e) {
            throw new RuntimeException("Invalid task: " + e.getLocalizedMessage());
        }
    }

    @Override
    public TaskModel findTaskById(UUID taskId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        return new TaskModel(task);
    }

    @Override
    public List<TaskModel> findAllTasksByUserId(UUID userId) {
        try {
            return taskRepository
                    .findTasksByUserId(userId)
                    .stream()
                    .map(TaskModel::new)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @Override
    public void updateTask(TaskModel updatedTask) {
        var taskToBeUpdated = taskRepository.findById(updatedTask.id()).orElseThrow(TaskNotFoundException::new);
        try {
            taskToBeUpdated.setTitle(updatedTask.title());
            taskToBeUpdated.setDescription(updatedTask.description());
            taskToBeUpdated.setPriority(updatedTask.priority());
            taskToBeUpdated.setStatus(updatedTask.status());
            taskRepository.save(taskToBeUpdated);
        } catch (Exception e) {
            throw new RuntimeException("Invalid task: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void deleteTask(TaskModel taskToBeDeleted) {
        try {
            taskRepository.delete(new TaskEntity(taskToBeDeleted));
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getLocalizedMessage());
        }
    }
}
