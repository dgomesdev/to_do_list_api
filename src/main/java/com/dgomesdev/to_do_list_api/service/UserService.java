package com.dgomesdev.to_do_list_api.service;

import com.dgomesdev.to_do_list_api.domain.model.User;

import java.util.UUID;

public interface UserService {

    void saveUser(User newUser);
    User findUserById(UUID userId);
    User findUserByUsername(String username);
    void updateUser(User updatedUser);
    void deleteUser(UUID userId);
}
