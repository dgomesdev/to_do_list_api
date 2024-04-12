package com.dgomesdev.to_do_list_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "tb_tasks")
@Getter
@Setter
public final class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        private String title;
        private String description;
        private String priority;
        private String status;
        private UUID userId;
}