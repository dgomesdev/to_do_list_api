package com.dgomesdev.to_do_list_api.service;

import com.dgomesdev.to_do_list_api.domain.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskCrudService extends BaseCrudService<Task, UUID> {

    List<Task> findAllTasksByUserId(UUID userId);
}