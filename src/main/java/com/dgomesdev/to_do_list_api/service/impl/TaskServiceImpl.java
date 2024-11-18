package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.data.repository.TaskRepository;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.service.interfaces.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl extends BaseServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskModel saveTask(TaskModel task) {
        try {
            var user = userRepository.findById(UUID.fromString(getUserId())).orElseThrow(UserNotFoundException::new);
            return new TaskModel(taskRepository.save(new TaskEntity(task, user)));
        } catch (Exception e) {
            throw new RuntimeException("Invalid task: " + e.getLocalizedMessage());
        }
    }

    @Override
    public TaskModel findTaskById(UUID taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        if (
                task.getUser().getId() != null
                && !task.getUser().getId().toString().equals(this.getUserId())
                && !this.getUserAuthorities().contains(UserAuthority.ADMIN)
        ) throw new UnauthorizedUserException();
        return new TaskModel(task);
    }

    @Override
    public TaskModel updateTask(UUID taskId, TaskModel task) {
        var existingTask = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        if (!existingTask.getUser().getId().toString().equals(this.getUserId()) && !this.getUserAuthorities().contains(UserAuthority.ADMIN))
            throw new UnauthorizedUserException();

        if (!Objects.equals(existingTask.getTitle(), task.getTitle())) {
            existingTask.setTitle(task.getTitle());
        }
        if (!Objects.equals(existingTask.getDescription(), task.getDescription())) {
            existingTask.setDescription(task.getDescription());
        }
        if (!Objects.equals(existingTask.getPriority(), task.getPriority())) {
            existingTask.setPriority(task.getPriority());
        }
        if (!Objects.equals(existingTask.getStatus(), task.getStatus())) {
            existingTask.setStatus(task.getStatus());
        }
        var updatedTask = taskRepository.save(existingTask);

        return new TaskModel(updatedTask);
    }

    @Override
    public void deleteTask(UUID taskId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        if (!task.getUser().getId().toString().equals(this.getUserId()) && !this.getUserAuthorities().contains(UserAuthority.ADMIN))
            throw new UnauthorizedUserException();
        taskRepository.delete(task);
    }
}
