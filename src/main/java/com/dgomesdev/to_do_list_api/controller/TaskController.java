package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.TaskResponseDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.TaskService;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/tasks")
@Tag(name = "Task controller", description = "Controller to manage the user's tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Save task", description = "Create task")
    public ResponseEntity<UserResponseDto> saveTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        var savedTask = taskService.saveTask(new TaskModel.Builder().fromRequest(taskRequestDto).build());
        var user = userService.findUserById(savedTask.getTaskId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponseDto(user));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Find task", description = "Find a specific task by ID")
    public ResponseEntity<TaskResponseDto> findTaskById(@PathVariable UUID taskId) {
        var foundTask = taskService.findTaskById(taskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new TaskResponseDto(foundTask));
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Update the task's data")
    public ResponseEntity<UserResponseDto> updateTask(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskRequestDto taskRequestDto
    ) {
        var updatedTask = taskService.updateTask(
                taskId,
                new TaskModel.Builder().fromRequest(taskRequestDto).build()
        );
        var user = userService.findUserById(updatedTask.getTaskId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(user));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Delete a task")
    public ResponseEntity<UserResponseDto> deleteTask(@PathVariable UUID taskId) {
        if (taskId == null) throw new NullPointerException("taskId cannot be null");
        var taskUser = taskService.deleteTask(taskId);
        var user = userService.findUserById(taskUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(user));
    }
}