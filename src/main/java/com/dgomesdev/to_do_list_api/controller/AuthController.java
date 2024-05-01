package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.AuthRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.service.impl.TokenServiceImpl;
import com.dgomesdev.to_do_list_api.service.impl.UserServiceImpl;
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

@CrossOrigin
@RestController
@RequestMapping("/")
@Tag(name = "Authentication controller", description = "Controller to manage the access to the app")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TokenServiceImpl tokenService;

    @PostMapping("register")
    @Operation(summary = "Register an user", description = "Create the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid user"),
            @ApiResponse(responseCode = "500", description = "Error while creating user")
    })
    public ResponseEntity<String> register(@RequestBody @Valid UserRequestDto userDto) {
        try {
            userService.saveUser(new User(userDto));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid user");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while creating user: " + getClass() + " " + e.getLocalizedMessage());
        }
    }

    @PostMapping("login")
    @Operation(summary = "Login", description = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful for user"),
            @ApiResponse(responseCode = "403", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while logging in")
    })
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        try {
            userService.findUserByUsername(authRequestDto.username());
            var usernamePassword = new UsernamePasswordAuthenticationToken(
                    authRequestDto.username(),
                    authRequestDto.password()
            );
            System.out.println(usernamePassword);
            var auth = authenticationManager.authenticate(usernamePassword);
            System.out.println("auth = " + auth);
            System.out.println("principal = " + auth.getPrincipal());
            var token = tokenService.generateToken((UserDetails) auth.getPrincipal());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Login successful for user " + authRequestDto.username() + " with token " + token);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getLocalizedMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while logging in: " + e.getClass() + " " + e.getLocalizedMessage());
        }
    }
}
