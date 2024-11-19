package com.dgomesdev.to_do_list_api.infra;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dgomesdev.to_do_list_api.domain.exception.TaskNotFoundException;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.dto.response.MessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDto> handleGeneralException(Exception exception) {
        HttpStatus httpStatus;
        if (exception.getClass() == UnauthorizedUserException.class
                || exception.getClass() == TokenExpiredException.class
                || exception.getClass() == JWTDecodeException.class
        ) httpStatus = HttpStatus.UNAUTHORIZED;
        else if (exception.getClass() == UserNotFoundException.class
                || exception.getClass() == TaskNotFoundException.class
        ) httpStatus = HttpStatus.NOT_FOUND;
        else httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(httpStatus).body(new MessageDto("An error occurred: " + exception.getMessage()));
    }
}