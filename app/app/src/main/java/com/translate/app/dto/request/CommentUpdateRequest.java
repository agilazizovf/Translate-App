package com.translate.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentUpdateRequest {

    private Integer id;

    @NotNull(message = "TranslationRecord ID cannot be null")
    private Integer translationId;

    @NotBlank(message = "Content cannot be blank")
    private String content;
}
