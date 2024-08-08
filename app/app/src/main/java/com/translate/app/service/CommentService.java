package com.translate.app.service;

import com.translate.app.dto.request.CommentUpdateRequest;
import com.translate.app.dto.response.CommentAddResponse;
import com.translate.app.dto.response.CommentUpdateResponse;
import com.translate.app.entity.Comment;
import com.translate.app.entity.Translation;
import com.translate.app.entity.User;
import com.translate.app.exception.AuthenticationException;
import com.translate.app.repository.CommentRepository;
import com.translate.app.repository.TranslationRepository;
import com.translate.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;

    // Existing methods...

    public List<Comment> getCommentsForTranslation(Integer translationId) {
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new RuntimeException("Translation not found"));

        return commentRepository.findByTranslation(translation);
    }

    public List<Comment> getCommentsForCurrentUser() {
        // Retrieve the current user's username from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        // Retrieve the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch and return comments for this user
        return commentRepository.findByUser(user);
    }

    public CommentAddResponse add(Integer translationRecordId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Translation translationRecord = translationRepository.findById(translationRecordId)
                .orElseThrow(() -> new RuntimeException("TranslationRecord not found"));

        // Initialize the comment with the current time for both createdAt and updatedAt
        Comment comment = new Comment(content, LocalDateTime.now(), LocalDateTime.now(), user, translationRecord);
        commentRepository.save(comment);

        // Prepare response
        CommentAddResponse response = new CommentAddResponse();
        response.setTranslationId(comment.getTranslation().getId());
        response.setContent(comment.getContent());

        return response;
    }

    public CommentUpdateResponse update(CommentUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if the comment belongs to the user
        if (!comment.getUser().equals(user)) {
            throw new AuthenticationException("You do not have permission to update this comment");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);

        // Prepare and return the response
        CommentUpdateResponse response = new CommentUpdateResponse();
        response.setTranslationId(comment.getTranslation().getId());
        response.setContent(comment.getContent());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }

    public void delete(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().equals(user)) {
            throw new AuthenticationException("You do not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
