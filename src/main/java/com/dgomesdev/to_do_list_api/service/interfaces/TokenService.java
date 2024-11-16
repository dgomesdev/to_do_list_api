package com.dgomesdev.to_do_list_api.service.interfaces;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;

public interface TokenService {

    String generateToken(UserModel user);
    UserModel getUserFromToken(String token);
}
