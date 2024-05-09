package com.dgomesdev.to_do_list_api.controller.dto.response;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.data.entity.UserEntity;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String password
) implements ResponseDto {
    public UserResponseDto(UserEntity userEntity) {
        this(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword()
        );
    }

    public UserResponseDto(UserModel userModel) {
        this(
                userModel.id(),
                userModel.username(),
                userModel.email(),
                userModel.password()
        );
    }
}