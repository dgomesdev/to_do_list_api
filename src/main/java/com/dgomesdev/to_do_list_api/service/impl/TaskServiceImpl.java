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
            UUID userId = UUID.fromString(getUserId());
            var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            return new TaskModel.Builder()
                    .fromEntity(taskRepository.save(new TaskEntity(task, user)))
                    .build();
    }

    @Override
    public TaskModel findTaskById(UUID taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (
                !task.getUser().getId().toString().equals(this.getUserId())
                && !this.getUserAuthorities().contains(UserAuthority.ADMIN)
        ) throw new UnauthorizedUserException(UUID.fromString(getUserId()));
        return new TaskModel.Builder()
                .fromEntity(task)
                .build();
    }

    @Override
    public TaskModel updateTask(UUID taskId, TaskModel task) {
        var existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        var taskUser = existingTask.getUser().getId().toString();
        var userFromToken = this.getUserId();
        var isUserInvalid = !Objects.equals(taskUser, userFromToken);
        if (isUserInvalid) throw new UnauthorizedUserException(UUID.fromString(userFromToken));

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

        return new TaskModel.Builder()
                .fromEntity(updatedTask)
                .build();
    }

    @Override
    public UUID deleteTask(UUID taskId) {
        var task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

        var taskUser = task.getUser().getId().toString();
        var userFromToken = this.getUserId();
        var isUserInvalid = !Objects.equals(taskUser, userFromToken);

        if (isUserInvalid) throw new UnauthorizedUserException(UUID.fromString(userFromToken));
        taskRepository.delete(task);
        return UUID.fromString(userFromToken);
    }
}
