package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.controller.dto.request.UserRequestDto;
import com.dgomesdev.to_do_list_api.data.entity.UserEntity;

import java.util.UUID;

public record UserModel(
        UUID id,
        String username,
        String email,
        String password,
        UserRole userRole
) {
    public UserModel(UserEntity userEntity) {
        this(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getUserRole()
        );
    }

    public UserModel(UUID userID, UserRequestDto userRequestDto, UserRole userRole) {
        this(
                userID,
                userRequestDto.username(),
                userRequestDto.email(),
                userRequestDto.password(),
                userRole
        );
    }


}
