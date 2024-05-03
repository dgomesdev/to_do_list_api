package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.TaskRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.TaskResponseDto;
import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.Task;
import com.dgomesdev.to_do_list_api.service.impl.TaskServiceImpl;
import com.dgomesdev.to_do_list_api.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
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
    private TaskServiceImpl taskService;

    @Autowired
    private UserServiceImpl userService;

    @PostMapping
    @Operation(summary = "Save task", description = "Create a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task saved"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Error while saving the task"),
    })
    public ResponseEntity<String> saveTask(
            @RequestBody @Valid TaskRequestDto taskRequestDto,
            HttpServletRequest request
    ) {
        try {
            var userId = userService.findUserById((UUID) request.getAttribute("userId")).getId();
            taskService.saveTask(new Task(taskRequestDto, userId));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Task created successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized user");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving the task: " + e.getLocalizedMessage());
        }
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Find task by Id", description = "Find a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Error while saving the task")
    })
    public ResponseEntity<Object> findTaskById(
            @PathVariable UUID taskId,
            HttpServletRequest request
    ) {
        try {
            var foundTask = taskService.findTaskById(taskId);
            if (!foundTask.getUserId().equals(request.getAttribute("userId"))) throw new UnauthorizedUserException();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponseDto(foundTask));
        } catch (UnauthorizedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getLocalizedMessage());
        } catch (TaskNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Task not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving task: " + e.getLocalizedMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Find all tasks", description = "List all the user's tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Error while listing the tasks")
    })
    public ResponseEntity<Object> findAllTasks(
            HttpServletRequest request
    ) {
        try {
            UUID userID = (UUID) request.getAttribute("userId");
            userService.findUserById(userID);
            var tasksList = taskService
                    .findAllTasksByUserId(userID)
                    .stream()
                    .map(TaskResponseDto::new)
                    .toList();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(tasksList);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized user");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while listing the tasks: " + e.getLocalizedMessage());
        }
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Error while updating the task"),
    })
    public ResponseEntity<String> updateTask(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskRequestDto taskRequestDto,
            HttpServletRequest request
    ) {
        try {
            var foundTask = taskService.findTaskById(taskId);
            if (!foundTask.getUserId().equals(request.getAttribute("userId"))) throw new UnauthorizedUserException();
            BeanUtils.copyProperties(taskRequestDto, foundTask);
            taskService.updateTask(foundTask);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Task updated successfully");
        } catch (UnauthorizedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getLocalizedMessage());
        } catch (TaskNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getLocalizedMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while updating the task: " + e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Delete a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Error while deleting the task")
    })
    public ResponseEntity<String> deleteTask(
            @PathVariable UUID taskId,
            HttpServletRequest request
    ) {
        try {
            var foundTask = taskService.findTaskById(taskId);
            if (!foundTask.getUserId().equals(request.getAttribute("userId"))) throw new UnauthorizedUserException();
            taskService.deleteTask(foundTask);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Task deleted successfully");
        } catch (UnauthorizedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getLocalizedMessage());
        } catch (TaskNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getLocalizedMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while deleting the task: " + e.getLocalizedMessage());
        }
    }
}