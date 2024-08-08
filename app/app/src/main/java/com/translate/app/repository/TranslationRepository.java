package com.translate.app.repository;

import com.translate.app.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TranslationRepository extends JpaRepository<Translation, Integer> {

    Translation findBySourceLanguageAndTargetLanguageAndSourceText(
            String sourceLanguage, String targetLanguage, String sourceText);
}
