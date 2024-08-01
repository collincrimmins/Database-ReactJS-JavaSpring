package com.mywebsite.database_javaspring_reactjs.exceptions;

import org.springframework.http.HttpStatus;

public class AuthFailedException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.FOUND;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public AuthFailedException() {
        super("auth-failed");
    }
}
