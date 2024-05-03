package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.service.impl.TokenServiceImpl;
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
@RequestMapping("/user")
@Tag(name = "User controller", description = "Controller to manage the users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TokenServiceImpl tokenService;

    @GetMapping("/{userId}")
    @Operation(summary = "Find user by Id", description = "Find a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while finding the user")
    })
    public ResponseEntity<Object> findUserById(@PathVariable UUID userId) {
        try {
            var userEntity = userService.findUserById(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new UserResponseDto(userEntity));
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getLocalizedMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while finding the user: " + e.getLocalizedMessage());
        }
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update an user", description = "Update the user's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "500", description = "Error while updating the user")
    })
    public ResponseEntity<String> updateUser(@PathVariable UUID userId, @RequestBody @Valid UserRequestDto userRequestDto, HttpServletRequest request) {
        try {
            if (!request.getAttribute("userId").equals(userId)) throw new UnauthorizedUserException();
            var userEntity = userService.findUserById(userId);
            BeanUtils.copyProperties(userRequestDto, userEntity);
            userService.updateUser(userEntity);
            var token = tokenService.generateToken(userEntity);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("User updated successfully. New token: " + token);
        } catch (UnauthorizedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getLocalizedMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid user");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while updating the user: " + e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete an user", description = "Delete the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while deleting the user")
    })
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId, HttpServletRequest request) {
        try {
            if (!request.getAttribute("userId").equals(userId)) throw new UnauthorizedUserException();
            userService.deleteUser(userId);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("User deleted successfully");
        } catch (UnauthorizedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getLocalizedMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getLocalizedMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while deleting the user: " + e.getLocalizedMessage());
        }
    }
}