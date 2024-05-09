package com.dgomesdev.to_do_list_api.controller.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;

import java.util.UUID;

public record TaskResponseDto(
        UUID id,
        String title,
        String description,
        Priority priority,
        Status status
) implements ResponseDto {
    public TaskResponseDto(TaskEntity taskEntity) {
        this(
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getPriority(),
                taskEntity.getStatus()
        );
    }

    public TaskResponseDto(TaskModel taskModel) {
        this(
                taskModel.id(),
                taskModel.title(),
                taskModel.description(),
                taskModel.priority(),
                taskModel.status()
        );
    }
}