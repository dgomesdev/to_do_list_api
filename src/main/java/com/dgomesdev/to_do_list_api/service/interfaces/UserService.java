package com.dgomesdev.to_do_list_api.service.interfaces;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

import java.util.UUID;

public interface UserService {

    void saveUser(UserModel userModel);
    UserModel findUserById(UUID userId);
    UserModel findUserByUsername(String username);
    String updateUser(UserModel updatedUser);
    void deleteUser(UUID userId);
}
