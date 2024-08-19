package com.translate.app.model.dto.request;

import lombok.Data;

@Data
public class PhraseAddRequest {

    private Integer id;
    private String content;
    private Integer phraseBookId;
}
