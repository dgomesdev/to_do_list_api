package com.dgomesdev.to_do_list_api.controller.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.User;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String password
) {
    public UserResponseDto(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );
    }
}