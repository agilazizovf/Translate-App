package com.translate.app.model.dto.request;

import lombok.Data;

@Data
public class PhraseBookAddRequest {

    private Integer id;
    private String name;
    private Integer categoryId;
}
