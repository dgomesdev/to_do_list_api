package com.dgomesdev.to_do_list_api.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        String username,
        String password,
        List<TaskResponseDto> tasks
) {

    public UserResponseDto(UserModel user) {
        this(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getTasks().stream().map(TaskResponseDto::new).toList()
        );
    }
}