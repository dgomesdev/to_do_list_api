package com.dgomesdev.to_do_list_api.service;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

public interface TokenService {

    String generateToken(String username);
    String validateToken(HttpServletRequest request);
    Instant getTokenExpirationDate();
}
