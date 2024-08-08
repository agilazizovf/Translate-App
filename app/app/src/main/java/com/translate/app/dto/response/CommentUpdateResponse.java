package com.translate.app.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentUpdateResponse {

    private Integer translationId;

    private String content;

    private LocalDateTime updatedAt;
}
