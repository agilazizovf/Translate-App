package com.translate.app.dao.repository;

import com.translate.app.dao.entity.Comment;
import com.translate.app.dao.entity.Translation;
import com.translate.app.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTranslation(Translation translation);
}
