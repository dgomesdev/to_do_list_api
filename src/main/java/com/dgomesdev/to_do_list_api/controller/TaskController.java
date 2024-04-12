package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.TaskResponseDto;
import com.dgomesdev.to_do_list_api.domain.model.Task;
import com.dgomesdev.to_do_list_api.service.impl.TaskCrudServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/tasks")
@Tag(name = "Task controller", description = "Controller to manage the user's tasks")
public class TaskController {

    @Autowired
    private TaskCrudServiceImpl taskService;

    @PostMapping
    @Operation(summary = "Save task", description = "Create a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task saved"),
            @ApiResponse(responseCode = "400", description = "Error while saving the task"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskResponseDto> saveTask(@RequestBody TaskRequestDto taskRequestDto, HttpServletRequest request) {
        var taskEntity = new Task();
        BeanUtils.copyProperties(taskRequestDto, taskEntity);
        UUID userId = (UUID) request.getAttribute("userId");
        taskEntity.setUserId(userId);
        taskService.save(taskEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponseDto(taskEntity));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Find task by Id", description = "Find a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskResponseDto> findTaskById(@PathVariable UUID taskId, HttpServletRequest request) {
        var taskEntity = taskService.findById(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(new TaskResponseDto(taskEntity));
    }

    @GetMapping
    @Operation(summary = "Find all tasks", description = "List all the user's tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found"),
            @ApiResponse(responseCode = "400", description = "Error on getting the tasks"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskResponseDto>> findAllTasks(HttpServletRequest request) {
        var tasksList = taskService
                .findAllTasksByUserId((UUID) request.getAttribute("userId"))
                .stream()
                .map(TaskResponseDto::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(tasksList);
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable UUID taskId, @RequestBody TaskRequestDto taskRequestDto, HttpServletRequest request) {
        var taskEntity = taskService.findById(taskId);
        BeanUtils.copyProperties(taskRequestDto, taskEntity);
        taskService.update(taskEntity);
        return ResponseEntity.status(HttpStatus.OK).body(new TaskResponseDto(taskEntity));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Delete a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.delete(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
