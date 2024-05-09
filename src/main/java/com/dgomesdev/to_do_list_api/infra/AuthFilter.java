package com.dgomesdev.to_do_list_api.infra;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.impl.TokenServiceImpl;
import com.dgomesdev.to_do_list_api.service.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TokenServiceImpl tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            var username = tokenService.validateToken(request);
            UserDetails userDetails = userService.loadUserByUsername(username);
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserModel user = userService.findUserByUsername(username);
            request.setAttribute("userId", user.id());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}