package com.dgomesdev.to_do_list_api.service.interfaces;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

import java.util.UUID;

public interface UserService {

    UserModel saveUser(UserModel newUser);
    UserModel findUserById(UUID userId);
    UserModel updateUser(UserModel updatedUser, UUID userId);
    void deleteUser(UUID userId);
}
