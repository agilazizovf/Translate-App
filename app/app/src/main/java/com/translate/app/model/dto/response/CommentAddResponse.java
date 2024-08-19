package com.translate.app.model.dto.response;

import lombok.Data;

@Data
public class CommentAddResponse {

    private Integer translationId;
    private String content;
}
