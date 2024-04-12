package com.dgomesdev.to_do_list_api.service;

import com.dgomesdev.to_do_list_api.domain.model.User;

import java.time.Instant;

public interface TokenService {

    String generateToken(User user);
    String validateToken(String token);
    Instant getTokenExpirationDate();
}
