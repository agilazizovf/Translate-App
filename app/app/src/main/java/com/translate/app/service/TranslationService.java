package com.translate.app.service;

import com.translate.app.entity.Translation;
import com.translate.app.entity.User;
import com.translate.app.exception.AuthenticationException;
import com.translate.app.exception.EntityNotFoundException;
import com.translate.app.repository.TranslationRepository;
import com.translate.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final UserRepository userRepository;

    // Case-insensitive maps
    private final Map<String, Map<String, Map<String, String>>> translations = new HashMap<>();

    public void addTranslation(Translation translation) {
        User user = getCurrentUser();
        translation.setUser(user);
        translationRepository.save(translation);

        // Normalize case: convert to lowercase
        String sourceLanguage = translation.getSourceLanguage();
        String targetLanguage = translation.getTargetLanguage();
        String sourceText = translation.getSourceText().toLowerCase();
        String targetText = translation.getTargetText().toLowerCase();

        // Update the in-memory map with normalized case
        Map<String, Map<String, String>> targetLangMap = translations
                .computeIfAbsent(sourceLanguage, k -> new HashMap<>());
        targetLangMap
                .computeIfAbsent(targetLanguage, k -> new HashMap<>())
                .put(sourceText, targetText);
    }


    @Transactional
    public void updateTranslation(Integer id, Translation updatedTranslation) {

        User currentUser = getCurrentUser();
        // Find the existing translation
        Translation existingTranslation = translationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found with ID: " + id));

        if (!existingTranslation.getUser().equals(currentUser)) {
            throw new AuthenticationException("You are not authorized to update this translation.");
        }

        // Update fields
        existingTranslation.setSourceLanguage(updatedTranslation.getSourceLanguage());
        existingTranslation.setTargetLanguage(updatedTranslation.getTargetLanguage());
        existingTranslation.setSourceText(updatedTranslation.getSourceText());
        existingTranslation.setTargetText(updatedTranslation.getTargetText());

        // Save the updated translation
        translationRepository.save(existingTranslation);
    }

    public String translate(String sourceLanguage, String targetLanguage, String sourceText) {
        // Normalize case: convert to lowercase
        String sourceLang = sourceLanguage.toLowerCase();
        String targetLang = targetLanguage.toLowerCase();
        String normalizedSourceText = sourceText.toLowerCase();

        // Check the in-memory map first
        String translationFromCache = getFromCache(sourceLang, targetLang, normalizedSourceText);
        if (translationFromCache != null) {
            return translationFromCache;
        }

        // Retrieve the translation from the database if not found in the cache
        Translation translation = translationRepository.findBySourceLanguageAndTargetLanguageAndSourceText(
                sourceLang, targetLang, sourceText);

        if (translation != null) {
            // Update the cache with the found translation
            addToCache(sourceLang, targetLang, normalizedSourceText, translation.getTargetText());
            return translation.getTargetText();
        }

        return "Translation not available.";
    }

    private String getFromCache(String sourceLang, String targetLang, String sourceText) {
        Map<String, Map<String, String>> targetLangMap = translations.get(sourceLang);
        if (targetLangMap != null) {
            Map<String, String> sourceTextMap = targetLangMap.get(targetLang);
            if (sourceTextMap != null) {
                return sourceTextMap.get(sourceText);
            }
        }
        return null;
    }

    private void addToCache(String sourceLang, String targetLang, String sourceText, String targetText) {
        Map<String, Map<String, String>> targetLangMap = translations
                .computeIfAbsent(sourceLang, k -> new HashMap<>());
        targetLangMap
                .computeIfAbsent(targetLang, k -> new HashMap<>())
                .put(sourceText, targetText);
    }

    @Transactional
    public void deleteTranslation(Integer id) {

        User currentUser = getCurrentUser();
        // Check if the translation exists
        Translation existingTranslation = translationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Translation not found with ID: " + id));

        if (!existingTranslation.getUser().equals(currentUser)) {
            throw new AuthenticationException("You are not authorized to update this translation.");
        }

        // Delete the translation
        translationRepository.deleteById(id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
