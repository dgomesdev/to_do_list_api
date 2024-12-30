package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.regex.Pattern;

public abstract class BaseServiceImpl {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    protected boolean isEmailInvalid(String email) {
        if (email == null || email.isBlank()) {
            return true; // Null or empty emails are invalid
        }

        // Check basic format using regex
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (!pattern.matcher(email).matches()) {
            return true; // Doesn't match the general email structure
        }

        // Additional checks for edge cases
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        // Ensure local and domain parts are not empty
        if (localPart.isEmpty() || domainPart.isEmpty()) {
            return true;
        }

        // Ensure no consecutive dots
        if (email.contains("..")) {
            return true;
        }

        // Ensure domain part contains at least one dot and a valid TLD
        int lastDotIndex = domainPart.lastIndexOf('.');
        return lastDotIndex < 1 || lastDotIndex == domainPart.length() - 1;// All checks passed
    }

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