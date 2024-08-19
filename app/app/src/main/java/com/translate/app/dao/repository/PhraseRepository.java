package com.translate.app.dao.repository;

import com.translate.app.dao.entity.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhraseRepository extends JpaRepository<Phrase, Integer> {
}
