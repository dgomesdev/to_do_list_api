package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.AuthRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.ResponseDto;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
@Tag(name = "Authentication controller", description = "Controller to manage the access to the app")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("register")
    @Operation(summary = "Register an user", description = "Create the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "500", description = "Error while creating user")
    })
    public ResponseEntity<ResponseDto> register(@RequestBody @Valid UserRequestDto userDto) {
        try {
            userService.saveUser(new UserModel(UUID.randomUUID(), userDto, UserRole.USER));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new MessageDto("User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageDto("Invalid user"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Error while creating user: " + e.getLocalizedMessage()));
        }
    }

    @PostMapping("login")
    @Operation(summary = "Login", description = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful for user"),
            @ApiResponse(responseCode = "401", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while logging in")
    })
    public ResponseEntity<ResponseDto> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        try {
            userService.findUserByUsername(authRequestDto.username());
            var usernamePassword = new UsernamePasswordAuthenticationToken(authRequestDto.username(), authRequestDto.password());
            var auth = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((UserDetails) auth.getPrincipal());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new MessageDto("Login successful for user " + authRequestDto.username() + " with token " + token));
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(e.getLocalizedMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDto("Error while logging in: " + e.getClass() + " " + e.getLocalizedMessage()));
        }
    }
}
