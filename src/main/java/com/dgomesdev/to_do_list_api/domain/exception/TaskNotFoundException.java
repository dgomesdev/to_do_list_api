package com.dgomesdev.to_do_list_api.domain.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class TaskNotFoundException extends NoSuchElementException {
    public TaskNotFoundException(UUID taskId) {
        super("Task with taskId " + taskId + " not found");
    }
}