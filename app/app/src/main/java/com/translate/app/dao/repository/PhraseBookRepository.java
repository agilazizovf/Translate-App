package com.translate.app.dao.repository;

import com.translate.app.dao.entity.PhraseBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhraseBookRepository extends JpaRepository<PhraseBook, Integer> {
}
