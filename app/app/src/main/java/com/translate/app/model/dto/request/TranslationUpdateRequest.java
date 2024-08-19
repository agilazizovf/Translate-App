package com.translate.app.model.dto.request;

import lombok.Data;

@Data
public class TranslationUpdateRequest {

    private String sourceLanguage;
    private String targetLanguage;
    private String sourceText;
    private String targetText;
}
