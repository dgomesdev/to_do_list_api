package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.TaskModel;
import com.dgomesdev.to_do_list_api.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.dto.response.TaskResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.TaskService;
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

    @PostMapping
    @Operation(summary = "Save task", description = "Create task")
    public ResponseEntity<TaskResponseDto> saveTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
            var newTask = taskService.saveTask(new TaskModel.Builder().fromRequest(taskRequestDto).build());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new TaskResponseDto(newTask));
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
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskRequestDto taskRequestDto
    ) {
            var updatedTask = taskService.updateTask(
                    taskId,
                    new TaskModel.Builder().fromRequest(taskRequestDto).build());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponseDto(updatedTask));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Delete a task")
    public ResponseEntity<MessageDto> deleteTask(@PathVariable UUID taskId) {
            if (taskId != null )taskService.deleteTask(taskId);
            else throw new NullPointerException("taskId cannot be null");
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new MessageDto("Task deleted successfully"));
    }
}