package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskModel{

    private UUID taskId;
    private final String title;
    private final String description;
    private final Priority priority;
    private final Status status;

    public TaskModel(TaskRequestDto taskRequestDto) {
        this.title = taskRequestDto.title();
        this.description = taskRequestDto.description();
        this.priority = taskRequestDto.priority();
        this.status = taskRequestDto.status();
    }

    public TaskModel(TaskEntity taskEntity) {
        this.taskId = taskEntity.getId();
        this.title = taskEntity.getTitle();
        this.description = taskEntity.getDescription();
        this.priority = taskEntity.getPriority();
        this.status = taskEntity.getStatus();
    }
}
