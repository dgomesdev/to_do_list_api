package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.controller.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;

import java.util.UUID;

public record TaskModel(
        UUID id,
        String title,
        String description,
        Priority priority,
        Status status,
        UUID userId
) {
    public TaskModel(UUID taskId, TaskRequestDto taskRequestDto, UUID userId) {
        this(
                taskId,
                taskRequestDto.title(),
                taskRequestDto.description(),
                taskRequestDto.priority(),
                taskRequestDto.status(),
                userId
        );
    }

    public TaskModel(TaskEntity taskEntity) {
        this(
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getPriority(),
                taskEntity.getStatus(),
                taskEntity.getUserId()
        );
    }
}
