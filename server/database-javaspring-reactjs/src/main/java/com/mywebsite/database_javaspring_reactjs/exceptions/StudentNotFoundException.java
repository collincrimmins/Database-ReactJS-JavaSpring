package com.mywebsite.database_javaspring_reactjs.exceptions;

import org.springframework.http.HttpStatus;

// Student not found by ID/Email
public class StudentNotFoundException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public StudentNotFoundException() {
        super("not-found");
    }
}
