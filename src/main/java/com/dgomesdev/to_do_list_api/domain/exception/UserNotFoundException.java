package com.dgomesdev.to_do_list_api.domain.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class UserNotFoundException extends NoSuchElementException {
    public UserNotFoundException(UUID userId) {
        super("User with userId " + userId + " not found");
    }

    public UserNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}