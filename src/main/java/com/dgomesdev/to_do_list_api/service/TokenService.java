package com.dgomesdev.to_do_list_api.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

public interface TokenService {

    String generateToken(UserDetails user);
    String validateToken(HttpServletRequest request);
    Instant getTokenExpirationDate();
}
