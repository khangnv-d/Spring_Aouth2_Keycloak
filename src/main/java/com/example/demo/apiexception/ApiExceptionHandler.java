package com.example.demo.apiexception;

import com.example.demo.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<Object> handleApiRequestException(RequestException e) {
        // 1. Create payload containing exception details
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ApiResponse apiException = new ApiResponse(
                e.getMessage(),
                badRequest
        );
        // 2. Return response entity
        return new ResponseEntity<>(apiException, badRequest);
    }
}

