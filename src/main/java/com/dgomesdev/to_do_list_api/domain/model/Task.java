package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.controller.dto.request.TaskRequestDto;
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
public final class Task {
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

        public Task(TaskRequestDto taskRequestDto, UUID userId) {
                this.title = taskRequestDto.title();
                this.description = taskRequestDto.description();
                this.priority = taskRequestDto.priority();
                this.status = taskRequestDto.status();
                this.userId = userId;
        }
}