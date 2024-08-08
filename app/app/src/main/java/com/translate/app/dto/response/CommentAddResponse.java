package com.translate.app.dto.response;

import lombok.Data;

@Data
public class CommentAddResponse {

    private Integer translationId;
    private String content;
}
