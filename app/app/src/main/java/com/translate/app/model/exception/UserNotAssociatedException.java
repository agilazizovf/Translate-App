package com.translate.app.model.exception;

public class UserNotAssociatedException extends RuntimeException{

    public UserNotAssociatedException(String message) {
        super(message);
    }
}
