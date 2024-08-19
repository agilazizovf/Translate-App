package com.translate.app.model.exception;

public class AuthenticationException extends RuntimeException{

    public AuthenticationException(String message) {
        super(message);
    }
}
