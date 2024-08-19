package com.translate.app.model.exception;

public class TranslationNotFoundException extends RuntimeException{

    public TranslationNotFoundException(String message) {
        super(message);
    }
}
