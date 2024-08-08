package com.translate.app.controller;

import com.translate.app.dto.request.CommentAddRequest;
import com.translate.app.dto.request.CommentUpdateRequest;
import com.translate.app.dto.response.CommentAddResponse;
import com.translate.app.dto.response.CommentUpdateResponse;
import com.translate.app.entity.Comment;
import com.translate.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentAddResponse> addComment(@RequestBody CommentAddRequest request) {
        CommentAddResponse response = commentService.add(request.getTranslationId(), request.getContent());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CommentUpdateResponse> updateComment(@RequestBody CommentUpdateRequest request) {
        CommentUpdateResponse response = commentService.update(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{translationId}")
    public ResponseEntity<List<Comment>> getCommentsForTranslation(@PathVariable Integer translationId) {
        List<Comment> comments = commentService.getCommentsForTranslation(translationId);
        return ResponseEntity.ok(comments);
    }
}
