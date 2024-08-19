package com.translate.app.model.exception;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
