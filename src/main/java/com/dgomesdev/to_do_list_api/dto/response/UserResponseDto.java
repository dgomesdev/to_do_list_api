package com.dgomesdev.to_do_list_api.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        String username,
        List<TaskResponseDto> tasks,
        String token
) {

    public UserResponseDto(UserModel user) {
        this(
                user.getUserId(),
                user.getUsername(),
                user.getTasks().stream().map(TaskResponseDto::new).toList(),
                user.getToken()
        );
    }
}