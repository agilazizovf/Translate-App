package com.translate.app.service.impl;

import com.translate.app.dao.entity.Translation;
import com.translate.app.dao.entity.User;
import com.translate.app.dao.repository.TranslationRepository;
import com.translate.app.dao.repository.UserRepository;
import com.translate.app.model.exception.*;
import com.translate.app.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;

    private final Map<String, Map<String, Map<String, String>>> translations = new HashMap<>();

    @Override
    public void addTranslation(Translation translation) {
        User user = getCurrentUser();
        translation.setUser(user);
        translation.setLikes(0);
        translation.setDislikes(0);
        translation.setViews(0); // Ensure views are initialized

        translationRepository.save(translation);

        // Normalize case: convert to lowercase
        String sourceLanguage = translation.getSourceLanguage().toLowerCase();
        String targetLanguage = translation.getTargetLanguage().toLowerCase();
        String sourceText = translation.getSourceText().toLowerCase();
        String targetText = translation.getTargetText().toLowerCase();

        // Update the in-memory cache
        Map<String, Map<String, String>> targetLangMap = translations
                .computeIfAbsent(sourceLanguage, k -> new HashMap<>());
        targetLangMap
                .computeIfAbsent(targetLanguage, k -> new HashMap<>())
                .put(sourceText, targetText);
    }

    @Override
    @Transactional
    public void updateTranslation(Integer id, Translation updatedTranslation) {
        User currentUser = getCurrentUser();
        Translation existingTranslation = translationRepository.findById(id)
                .orElseThrow(() -> new TranslationNotFoundException("Translation not found with ID: " + id));

        if (!existingTranslation.getUser().equals(currentUser)) {
            throw new AuthenticationException("You are not authorized to update this translation.");
        }

        existingTranslation.setSourceLanguage(updatedTranslation.getSourceLanguage());
        existingTranslation.setTargetLanguage(updatedTranslation.getTargetLanguage());
        existingTranslation.setSourceText(updatedTranslation.getSourceText());
        existingTranslation.setTargetText(updatedTranslation.getTargetText());

        translationRepository.save(existingTranslation);
    }

    @Override
    public String translate(String sourceLanguage, String targetLanguage, String sourceText) {
        String normalizedSourceLang = sourceLanguage.toLowerCase();
        String normalizedTargetLang = targetLanguage.toLowerCase();
        String normalizedSourceText = sourceText.toLowerCase();

        // Check the cache first
        String cachedTranslation = getFromCache(normalizedSourceLang, normalizedTargetLang, normalizedSourceText);
        if (cachedTranslation != null) {
            return cachedTranslation;
        }

        // Retrieve from database if not in cache
        Translation translation = translationRepository.findBySourceLanguageAndTargetLanguageAndSourceText(
                normalizedSourceLang, normalizedTargetLang, normalizedSourceText);

        if (translation != null) {
            addToCache(normalizedSourceLang, normalizedTargetLang, normalizedSourceText, translation.getTargetText());
            return translation.getTargetText();
        }

        return "Translation not available.";
    }

    private String getFromCache(String sourceLang, String targetLang, String sourceText) {
        return translations.getOrDefault(sourceLang, new HashMap<>())
                .getOrDefault(targetLang, new HashMap<>())
                .get(sourceText);
    }

    private void addToCache(String sourceLang, String targetLang, String sourceText, String targetText) {
        translations.computeIfAbsent(sourceLang, k -> new HashMap<>())
                .computeIfAbsent(targetLang, k -> new HashMap<>())
                .put(sourceText, targetText);
    }

    @Override
    @Transactional
    public void deleteTranslation(Integer id) {
        User currentUser = getCurrentUser();
        Translation existingTranslation = translationRepository.findById(id)
                .orElseThrow(() -> new TranslationNotFoundException("Translation not found with ID: " + id));

        if (!existingTranslation.getUser().equals(currentUser)) {
            throw new AuthenticationException("You are not authorized to delete this translation.");
        }

        translationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void likeTranslation(Integer translationId) {
        User currentUser = getCurrentUser();
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new TranslationNotFoundException("Translation not found with ID: " + translationId));

        if (!translation.getLikedByUsers().contains(currentUser.getId())) {
            if (translation.getLikes() == null) {
                translation.setLikes(0);
            }
            translation.getLikedByUsers().add(currentUser.getId());
            translation.setLikes(translation.getLikes() + 1);
            translationRepository.save(translation);
        } else {
            throw new UserLikeException("User has already liked this translation.");
        }
    }

    @Override
    @Transactional
    public void dislikeTranslation(Integer translationId) {
        User currentUser = getCurrentUser();
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new TranslationNotFoundException("Translation not found with ID: " + translationId));

        if (!translation.getDislikedByUsers().contains(currentUser.getId())) {
            if (translation.getDislikes() == null) {
                translation.setLikes(0);
            }
            translation.getDislikedByUsers().add(currentUser.getId());
            translation.setDislikes(translation.getDislikes() + 1);
            translationRepository.save(translation);
        } else {
            throw new UserLikeException("User has already disliked this translation.");
        }
    }

    @Override
    @Transactional
    public void findTranslationById(Integer translationId) {
        User currentUser = getCurrentUser();
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new TranslationNotFoundException("Translation not found with ID: " + translationId));

        if (!translation.getViewedByUsers().contains(currentUser.getId())) {
            translation.getViewedByUsers().add(currentUser.getId());
            translation.setViews(translation.getViews() + 1);
            translationRepository.save(translation);
        } else {
            throw new AlreadyExistsException("User has already viewed this translation.");
        }
    }

    @Override
    @Transactional
    public void addToFavorites(Integer translationId) {
        User currentUser = getCurrentUser();
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new TranslationNotFoundException("Translation not found with ID: "+translationId));

        if (currentUser.getFavoriteTranslations().contains(translation)) {
            throw new AlreadyExistsException("Translation is already in favorites");
        }

        currentUser.getFavoriteTranslations().add(translation);
        userRepository.save(currentUser);
    }

    @Override
    public Set<Translation> getFavoriteTranslations(User user) {
        // Ensure the user object is not null and is properly loaded
        User loadedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return loadedUser.getFavoriteTranslations();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
