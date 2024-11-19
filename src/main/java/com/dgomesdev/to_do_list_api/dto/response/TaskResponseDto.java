package com.dgomesdev.to_do_list_api.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;

import java.util.UUID;

public record TaskResponseDto(
        UUID id,
        String title,
        String description,
        Priority priority,
        Status status
) {
    public TaskResponseDto(TaskModel task) {
        this(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus()
        );
    }
}