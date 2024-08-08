package com.translate.app.repository;

import com.translate.app.entity.Comment;
import com.translate.app.entity.Translation;
import com.translate.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByUser(User user);
    List<Comment> findByTranslation(Translation translation);
}
