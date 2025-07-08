package com.example.blog.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    private String errMessage;

    public CustomException(ErrorCode errorCode, String errMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errMessage = errMessage;
    }
}
