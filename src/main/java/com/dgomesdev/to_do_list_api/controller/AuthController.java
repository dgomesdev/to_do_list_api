package com.dgomesdev.to_do_list_api.controller;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import com.dgomesdev.to_do_list_api.dto.response.UserResponseDto;
import com.dgomesdev.to_do_list_api.service.interfaces.EmailService;
import com.dgomesdev.to_do_list_api.service.interfaces.RecoverPasswordService;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/")
@Tag(name = "Authentication controller", description = "Controller to manage the access to the app")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private RecoverPasswordService recoverPasswordService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailService emailService;

    @PostMapping("register")
    @Operation(summary = "Register", description = "Create user")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto user) {
        UserModel savedUser = userService.saveUser(new UserModel.Builder()
                .withUsername(user.username())
                .withPassword(user.password())
                .withEmail(user.email().trim().toLowerCase())
                .withUserAuthorities(Set.of(UserAuthority.USER))
                .build()
        );
        emailService.sendWelcomeMail(user.email(), user.username());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponseDto(savedUser));
    }

    @PostMapping("login")
    @Operation(summary = "Login", description = "User login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.email().trim().toLowerCase(), user.password())
        );
        var loggedUser = (UserModel) authentication.getPrincipal();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDto(loggedUser));
    }

    @PostMapping("recoverPassword")
    @Operation(summary = "recoverPassword", description = "Recover Password")
    public ResponseEntity<MessageDto> recoverPassword(@RequestBody UserRequestDto user) {
        var foundUser = userService.findUserByEmail(user.email().trim().toLowerCase());
        System.out.println("Recover password: " + foundUser.getEmail());
        var recoveryPasswordCode = recoverPasswordService.generateCode(foundUser.getUserId());
        emailService.sendResetPasswordMail(user.email(), foundUser.getUsername(), recoveryPasswordCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageDto("Recovery code sent by mail"));
    }

    @GetMapping("reset-password")
    public void redirectToApp(@RequestParam String code, HttpServletResponse response) throws IOException {
        String appLink = "task-list-app://reset-password?code=" + code;
        response.sendRedirect(appLink);
    }


    @PostMapping("resetPassword/{recoveryCode}")
    @Operation(summary = "Reset password", description = "Reset password")
    public ResponseEntity<MessageDto> resetPassword(@PathVariable String recoveryCode, @RequestBody UserRequestDto user) {
        var foundUser = userService.findUserByEmail(user.email().trim().toLowerCase());
        System.out.println("Reset password: " + foundUser.getEmail());
        recoverPasswordService.validateCode(foundUser.getUserId(), recoveryCode);
        userService.resetPassword(foundUser.getUserId(), user.password());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageDto("Password updated successfully"));
    }
}