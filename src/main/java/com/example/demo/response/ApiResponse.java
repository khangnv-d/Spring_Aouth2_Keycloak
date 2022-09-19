package com.example.demo.response;

import org.springframework.http.HttpStatus;

public class ApiResponse {
    private String message;
    private HttpStatus httpStatus;

    public ApiResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
