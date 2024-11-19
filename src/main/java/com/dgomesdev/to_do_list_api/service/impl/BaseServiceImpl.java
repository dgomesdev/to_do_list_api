package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public abstract class BaseServiceImpl {

    private Authentication getSecurityContextAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected String getUserId() {
        return getSecurityContextAuthentication().getPrincipal().toString();
    }

    protected List<UserAuthority> getUserAuthorities() {
        return getSecurityContextAuthentication().getAuthorities()
                .stream()
                .map(authority -> UserAuthority.valueOf(authority.getAuthority()))
                .toList();
    }
}