package com.translate.app.service;

import com.translate.app.model.dto.request.CommentUpdateRequest;
import com.translate.app.model.dto.response.CommentAddResponse;
import com.translate.app.model.dto.response.CommentUpdateResponse;
import com.translate.app.dao.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsForTranslation(Integer translationId);
    CommentAddResponse add(Integer translationRecordId, String content);
    CommentUpdateResponse update(CommentUpdateRequest request);
    void delete(Integer id);
    void like(Integer commentId);
    void dislike(Integer commentId);

}
