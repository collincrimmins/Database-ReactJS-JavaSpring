package com.mywebsite.database_javaspring_reactjs.exceptions;

import org.springframework.http.HttpStatus;

// Student can not request to have this Email because it Already Exists
public class StudentEmailRequestAlreadyExists extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.FOUND;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public StudentEmailRequestAlreadyExists() {
        super("email-in-use");
    }
}
