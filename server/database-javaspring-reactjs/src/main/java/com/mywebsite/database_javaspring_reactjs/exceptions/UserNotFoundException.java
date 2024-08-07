package com.mywebsite.database_javaspring_reactjs.exceptions;

import org.springframework.http.HttpStatus;

// User not Found by ID/Email
public class UserNotFoundException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public UserNotFoundException() {
        super("user-not-found");
    }
}
