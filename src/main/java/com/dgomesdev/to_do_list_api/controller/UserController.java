package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "User controller", description = "Controller to manage the users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Find user", description = "Find a specific user by ID")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable UUID userId) {
        var user = userService.findUserById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(user));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update the user's data")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserRequestDto user
    ) {
        var updatedUser = userService.updateUser(
                new UserModel.Builder()
                        .withUserId(userId)
                        .withUsername(user.username())
                        .withPassword(user.password())
                        .withUserAuthorities(Set.of(UserAuthority.USER))
                        .build()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(updatedUser));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete the user")
    public ResponseEntity<MessageDto> deleteUser(@PathVariable UUID userId) {
        if (userId != null) userService.deleteUser(userId);
        else throw new NullPointerException("userId cannot be null");
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new MessageDto("User deleted successfully"));
    }
}