package com.dgomesdev.to_do_list_api.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        List<TaskResponseDto> tasks
) implements ResponseDto {

    public UserResponseDto(UserModel user) {
        this(
                user.getUserID(),
                user.getUsername(),
                user.getTasks().stream().map(TaskResponseDto::new).toList()
        );
    }
}