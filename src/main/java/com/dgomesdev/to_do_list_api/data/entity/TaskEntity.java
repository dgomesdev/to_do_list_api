package com.dgomesdev.to_do_list_api.data.entity;

import com.dgomesdev.to_do_list_api.controller.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.domain.model.Priority;
import com.dgomesdev.to_do_list_api.domain.model.Status;
import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "tb_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class TaskEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        @Column(nullable = false)
        private String title;
        private String description;
        @Column(nullable = false)
        private Priority priority;
        @Column(nullable = false)
        private Status status = Status.TO_BE_DONE;
        @Column(nullable = false)
        private UUID userId;

        public TaskEntity(TaskRequestDto taskRequestDto, UUID userId) {
                this.title = taskRequestDto.title();
                this.description = taskRequestDto.description();
                this.priority = taskRequestDto.priority();
                this.status = taskRequestDto.status();
                this.userId = userId;
        }

        public TaskEntity(TaskModel taskModel) {
                this.id = taskModel.id();
                this.title = taskModel.title();
                this.description = taskModel.description();
                this.priority = taskModel.priority();
                this.status = taskModel.status();
                this.userId = taskModel.userId();
        }
}