package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "User controller", description = "Controller to manage the users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{userId}")
    @Operation(summary = "Find user by Id", description = "Find a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while finding the user")
    })
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable UUID userId) {
        var user = userService.findUserById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(user));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update an user", description = "Update the user's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "500", description = "Error while updating the user")
    })
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserRequestDto user
    ) {
        var encodedPassword = passwordEncoder.encode(user.password());
        var updatedUser = userService.updateUser(
                userId,
                new UserModel.Builder()
                        .withUsername(user.username())
                        .withPassword(encodedPassword)
                        .withUserAuthorities(Set.of(UserAuthority.USER))
                        .build()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(updatedUser));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete an user", description = "Delete the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while deleting the user")
    })
    public ResponseEntity<MessageDto> deleteUser(@PathVariable UUID userId) {
        if (userId != null) userService.deleteUser(userId);
        else throw new NullPointerException("userId cannot be null");
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new MessageDto("User deleted successfully"));
    }
}