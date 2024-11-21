package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskModel {

    private final UUID taskId;
    private final String title;
    private final String description;
    private final Priority priority;
    private final Status status;

    private TaskModel(Builder builder) {
        this.taskId = builder.taskId;
        this.title = builder.title;
        this.description = builder.description;
        this.priority = builder.priority;
        this.status = builder.status;
    }

    public static class Builder {
        private UUID taskId;
        private String title;
        private String description;
        private Priority priority;
        private Status status;

        public Builder fromRequest(TaskRequestDto taskRequestDto) {
            this.title = taskRequestDto.title();
            this.description = taskRequestDto.description();
            this.priority = taskRequestDto.priority();
            this.status = taskRequestDto.status();
            return this;
        }

        public Builder fromEntity(TaskEntity taskEntity) {
            this.taskId = taskEntity.getId();
            this.title = taskEntity.getTitle();
            this.description = taskEntity.getDescription();
            this.priority = taskEntity.getPriority();
            this.status = taskEntity.getStatus();
            return this;
        }

        public TaskModel build() {
            return new TaskModel(this);
        }
    }
}