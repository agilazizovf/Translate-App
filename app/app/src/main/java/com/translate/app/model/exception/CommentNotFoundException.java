package com.translate.app.model.exception;

public class CommentNotFoundException extends RuntimeException{

    public CommentNotFoundException(String message) {
        super(message);
    }
}
