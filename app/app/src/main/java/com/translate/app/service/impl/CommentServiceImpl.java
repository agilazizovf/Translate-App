package com.translate.app.service.impl;

import com.translate.app.model.dto.request.CommentUpdateRequest;
import com.translate.app.model.dto.response.CommentAddResponse;
import com.translate.app.model.dto.response.CommentUpdateResponse;
import com.translate.app.dao.entity.Comment;
import com.translate.app.dao.entity.Translation;
import com.translate.app.dao.entity.User;
import com.translate.app.dao.repository.CommentRepository;
import com.translate.app.dao.repository.TranslationRepository;
import com.translate.app.dao.repository.UserRepository;
import com.translate.app.model.exception.*;
import com.translate.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;

    @Override
    public List<Comment> getCommentsForTranslation(Integer translationId) {
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new RuntimeException("Translation not found"));

        return commentRepository.findByTranslation(translation);
    }
    @Override
    public CommentAddResponse add(Integer translationRecordId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Translation translationRecord = translationRepository.findById(translationRecordId)
                .orElseThrow(() -> new TranslationNotFoundException("TranslationRecord not found"));

        Comment comment = new Comment(content, LocalDateTime.now(), LocalDateTime.now(), user, translationRecord);
        commentRepository.save(comment);

        CommentAddResponse response = new CommentAddResponse();
        response.setTranslationId(comment.getTranslation().getId());
        response.setContent(comment.getContent());

        return response;
    }

    @Override
    public CommentUpdateResponse update(Integer id, CommentUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getUser().equals(user)) {
            throw new AuthenticationException("You do not have permission to update this comment");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);

        CommentUpdateResponse response = new CommentUpdateResponse();
        response.setTranslationId(comment.getTranslation().getId());
        response.setContent(comment.getContent());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }

    @Override
    public void delete(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getUser().equals(user)) {
            throw new AuthenticationException("You do not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void like(Integer commentId) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getLikedByUsers().contains(currentUser.getId())) {
            if (comment.getLikes() == null) {
                comment.setLikes(0);
            }
            comment.getLikedByUsers().add(currentUser.getId());
            comment.setLikes(comment.getLikes() + 1);
            commentRepository.save(comment);
        } else {
            throw new UserLikeException("User has already liked this comment.");
        }
    }

    @Override
    @Transactional
    public void dislike(Integer commentId) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getDislikedByUsers().contains(currentUser.getId())) {
            if (comment.getDislikes() == null) {
                comment.setDislikes(0);
            }
            comment.getDislikedByUsers().add(currentUser.getId());
            comment.setDislikes(comment.getDislikes() + 1);
            commentRepository.save(comment);
        } else {
            throw new UserLikeException("User has already disliked this comment.");
        }


    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
