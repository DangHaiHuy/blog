package com.example.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(9001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(9002, "Unauthorized", HttpStatus.FORBIDDEN),
    INVALID_KEY(9003, "Invalid key", HttpStatus.BAD_REQUEST),
    NOT_FOUND(9004, "Not found", HttpStatus.NOT_FOUND),
    USER_LOCKED(9005, "User locked", HttpStatus.FORBIDDEN),
    EXISTED(9006,"Existed",HttpStatus.BAD_REQUEST),
    NOT_ACTIVATED(9007,"Not activated", HttpStatus.FORBIDDEN);

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
