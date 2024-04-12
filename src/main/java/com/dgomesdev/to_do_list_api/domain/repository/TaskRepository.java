package com.dgomesdev.to_do_list_api.domain.repository;

import com.dgomesdev.to_do_list_api.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findTasksByUserId(UUID userId);
}
