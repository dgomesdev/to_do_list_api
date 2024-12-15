package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.AuthResponseDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid UserRequestDto user) {
        UserModel savedUser = userService.saveUser(new UserModel.Builder()
                .withUsername(user.username())
                .withPassword(user.password())
                .withEmail(user.email())
                .withUserAuthorities(Set.of(UserAuthority.USER))
                .build()
        );
        String token = tokenService.generateToken(savedUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponseDto(new UserResponseDto(savedUser), token));
    }

    @PostMapping("login")
    @Operation(summary = "Login", description = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful for user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while logging in")
    })
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid UserRequestDto user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.username(), user.password())
        );
        var loggedUser = (UserModel) authentication.getPrincipal();
        var token = tokenService.generateToken(loggedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponseDto(new UserResponseDto(loggedUser), token));
    }

    @PostMapping("recoverPassword")
    @Operation(summary = "recoverPassword", description = "Recover Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temporary token to reset password"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error while recovering password")
    })
    public ResponseEntity<AuthResponseDto> recoverPassword(@RequestBody @Valid UserRequestDto user) {
        var foundUser = userService.findUserByEmail(user.email());
        var token = tokenService.generateToken(foundUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponseDto(new UserResponseDto(foundUser), token));
    }
}
