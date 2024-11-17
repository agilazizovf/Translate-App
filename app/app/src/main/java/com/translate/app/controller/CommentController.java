package com.translate.app.controller;

import com.translate.app.model.dto.request.CommentAddRequest;
import com.translate.app.model.dto.request.CommentUpdateRequest;
import com.translate.app.model.dto.response.CommentAddResponse;
import com.translate.app.model.dto.response.CommentUpdateResponse;
import com.translate.app.dao.entity.Comment;
import com.translate.app.model.exception.CommentNotFoundException;
import com.translate.app.model.exception.TranslationNotFoundException;
import com.translate.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CommentUpdateResponse> updateComment(Long id, @RequestBody CommentUpdateRequest request) {
        CommentUpdateResponse response = commentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{translationId}")
    public ResponseEntity<List<Comment>> getCommentsForTranslation(@PathVariable Integer translationId) {
        List<Comment> comments = commentService.getCommentsForTranslation(translationId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> like(@PathVariable Long id) {
        try {
            commentService.like(id);
            return ResponseEntity.ok("Comment liked successfully.");
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<String> dislike(@PathVariable Long id) {
        try {
            commentService.dislike(id);
            return ResponseEntity.ok("Comment disliked successfully.");
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
