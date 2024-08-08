package com.translate.app.dto.request;

import lombok.Data;

@Data
public class TranslationAddRequest {

    private String sourceLanguage;
    private String targetLanguage;
    private String sourceText;
    private String targetText;
}
