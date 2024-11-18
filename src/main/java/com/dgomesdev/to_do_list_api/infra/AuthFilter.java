package com.dgomesdev.to_do_list_api.infra;

import com.dgomesdev.to_do_list_api.service.interfaces.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var token = recoverToken(request);
        System.out.println(token);
        if (!token.isBlank()) {
            var user = tokenService.getUserFromToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), null, user.getAuthorities());
            System.out.println(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) throws IOException {
        var authHeader = request.getHeader("Authorization");
        System.out.println(authHeader);
        if (authHeader == null) throw new IOException("The token is null.");
        return authHeader.replace("Bearer ", "");
    }
}