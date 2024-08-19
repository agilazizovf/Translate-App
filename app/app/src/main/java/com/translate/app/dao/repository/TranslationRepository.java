package com.translate.app.dao.repository;

import com.translate.app.dao.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Integer> {

    Translation findBySourceLanguageAndTargetLanguageAndSourceText(
            String sourceLanguage, String targetLanguage, String sourceText);
}
