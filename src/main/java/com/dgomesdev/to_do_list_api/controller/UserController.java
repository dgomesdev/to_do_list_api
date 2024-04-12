package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.impl.UserCrudServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "User controller", description = "Controller to manage the users")
public class UserController {

    @Autowired
    private UserCrudServiceImpl userService;

    @GetMapping("/{userId}")
    @Operation(summary = "Find user by Id", description = "Find a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable UUID userId) {
        var userEntity = userService.findById(userId);
        return ResponseEntity.ok(new UserResponseDto(userEntity));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update an user", description = "Update the user's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID userId, @RequestBody UserRequestDto userRequestDto) {
        var userEntity = userService.findById(userId);
        BeanUtils.copyProperties(userRequestDto, userEntity);
        var updatedUser = userService.update(userEntity);
        return ResponseEntity.ok(new UserResponseDto(updatedUser));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete an user", description = "Delete teh user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}