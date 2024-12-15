package com.dgomesdev.to_do_list_api.domain.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserAuthority {
    USER,
    ADMIN;

    public static GrantedAuthority toGrantedAuthority(UserAuthority userAuthority) {
        return new SimpleGrantedAuthority(userAuthority.name());
    }

    public static UserAuthority fromGrantedAuthority(GrantedAuthority grantedAuthority) {
        return UserAuthority.valueOf(grantedAuthority.getAuthority());
    }
}