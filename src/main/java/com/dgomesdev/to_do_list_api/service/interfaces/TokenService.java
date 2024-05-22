package com.dgomesdev.to_do_list_api.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.UUID;

public interface TokenService {

    String generateToken(UserDetails user, UUID userId);
    Pair<String, String> validateToken(HttpServletRequest request);
    Instant getTokenExpirationDate();
}
