package com.dgomesdev.to_do_list_api.domain.exception;

import java.util.NoSuchElementException;

public class TaskNotFoundException extends NoSuchElementException {
    public TaskNotFoundException() {
        super("Task not found");
    }
}