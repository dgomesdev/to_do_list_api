package com.dgomesdev.to_do_list_api.domain.exception;

import java.util.UUID;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException(UUID userId) {
        super("Unauthorized access for user with id " + userId);
    }
}
