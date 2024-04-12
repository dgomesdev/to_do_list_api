package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.model.Task;
import com.dgomesdev.to_do_list_api.domain.repository.TaskRepository;
import com.dgomesdev.to_do_list_api.service.TaskCrudService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class TaskCrudServiceImpl implements TaskCrudService {

    private final TaskRepository taskRepository;

    public TaskCrudServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task findById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Task> findAllTasksByUserId(UUID userId) {
        return taskRepository.findTasksByUserId(userId);
    }

    @Override
    public Task update(Task updatedTask) {

        return taskRepository.save(updatedTask);
    }

    @Override
    public void delete(UUID taskId) {
        taskRepository.delete(
                taskRepository.findById(taskId).orElseThrow(NoSuchElementException::new)
        );
    }
}
