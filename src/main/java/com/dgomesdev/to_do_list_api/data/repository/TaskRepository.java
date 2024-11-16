package com.dgomesdev.to_do_list_api.data.repository;

import com.dgomesdev.to_do_list_api.data.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {}
