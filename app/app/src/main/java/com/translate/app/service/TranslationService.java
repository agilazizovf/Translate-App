package com.translate.app.service;

import com.translate.app.dao.entity.Translation;
import com.translate.app.dao.entity.User;

import java.util.Set;

public interface TranslationService {
    void addTranslation(Translation translation);
    void updateTranslation(Integer id, Translation updatedTranslation);
    String translate(String sourceLanguage, String targetLanguage, String sourceText);
    void deleteTranslation(Integer id);
    void likeTranslation(Integer translationId);
    void dislikeTranslation(Integer translationId);
    void findTranslationById(Integer translationId);
    void addToFavorites(Integer translationId);
    Set<Translation> getFavoriteTranslations(User user);

    User getCurrentUser();
}
