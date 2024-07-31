package com.mywebsite.database_javaspring_reactjs.exceptions;

import org.springframework.http.HttpStatus;

// This Email already Exists
public class UserEmailRequestAlreadyExists extends RuntimeException  {
    private HttpStatus httpStatus = HttpStatus.FOUND;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public UserEmailRequestAlreadyExists() {
        super("email-in-use");
    }
}