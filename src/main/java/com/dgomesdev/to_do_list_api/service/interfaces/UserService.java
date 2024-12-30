package com.dgomesdev.to_do_list_api.service.interfaces;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

import java.util.UUID;

public interface UserService {

    UserModel saveUser(UserModel newUser);
    UserModel findUserById(UUID userId);
    UserModel findUserByEmail(String email);
    UserModel updateUser(UserModel updatedUser);
    void resetPassword(UUID userId, String password);
    void deleteUser(UUID userId);
}
