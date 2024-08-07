package com.mywebsite.database_javaspring_reactjs.exceptions;

import org.springframework.http.HttpStatus;

// Post not found by ID
public class PostNotFoundException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public PostNotFoundException() {
        super("not-found");
    }
}
