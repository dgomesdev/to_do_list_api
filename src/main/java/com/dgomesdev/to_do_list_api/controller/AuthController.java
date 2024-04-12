package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.controller.dto.request.AuthRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.controller.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.service.impl.TokenServiceImpl;
import com.dgomesdev.to_do_list_api.service.impl.UserCrudServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/")
@Tag(name = "Authentication controller", description = "Controller to manage the access to the app")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserCrudServiceImpl userService;
    @Autowired
    private TokenServiceImpl tokenService;

    @PostMapping("register")
    @Operation(summary = "Register an user", description = "Create the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Error on user's creation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRequestDto userDto) {
        var userEntity = new User();
        BeanUtils.copyProperties(userDto, userEntity);
        var savedUser = userService.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDto(savedUser));
    }

    @PostMapping("login")
    @Operation(summary = "Login", description = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        var usernamePassword =
                new UsernamePasswordAuthenticationToken(authRequestDto.username(), authRequestDto.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var loggedInUser = (User) auth.getPrincipal();
        var token = tokenService.generateToken(loggedInUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Login successful for user " + loggedInUser.getUsername() + " with token " + token);
    }
}
