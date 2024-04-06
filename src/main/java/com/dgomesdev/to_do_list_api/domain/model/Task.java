package com.dgomesdev.to_do_list_api.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "tb_task")
@Getter
@Setter
public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id;
        String title;
        String description;
        String priority;
        String status;
}