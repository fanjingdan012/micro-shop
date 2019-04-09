package com.fjd.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(Exception e) {
        super(e);
    }

    public BusinessException(String message) {
        super(message);
    }
}
