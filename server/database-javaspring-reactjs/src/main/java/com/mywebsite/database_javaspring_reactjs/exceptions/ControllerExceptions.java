package com.mywebsite.database_javaspring_reactjs.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mywebsite.database_javaspring_reactjs.responses.JsonResponse;

@RestControllerAdvice
public class ControllerExceptions {
    // Model Invalid (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    // StudentNotFoundException
    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<JsonResponse> StudentNotFoundException(StudentNotFoundException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new JsonResponse(e.getMessage()));
    }

    // StudentEmailRequestAlreadyExists
    @ExceptionHandler(StudentEmailRequestAlreadyExists.class)
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<JsonResponse> StudentEmailRequestAlreadyExists(StudentEmailRequestAlreadyExists e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new JsonResponse(e.getMessage()));
    }

    // UserEmailRequestAlreadyExists
    @ExceptionHandler(UserEmailRequestAlreadyExists.class)
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<JsonResponse> UserEmailRequestAlreadyExists(UserEmailRequestAlreadyExists e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new JsonResponse(e.getMessage()));
    }

    // AuthFailedException
    @ExceptionHandler(AuthFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<JsonResponse> AuthFailedException(AuthFailedException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new JsonResponse(e.getMessage()));
    }
}
