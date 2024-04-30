package com.dgomesdev.to_do_list_api.domain.exception;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException() {
        super("Unauthorized user");
    }
}
