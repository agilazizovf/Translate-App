package com.translate.app.controller;

import com.translate.app.dao.entity.User;
import com.translate.app.model.dto.request.TranslationAddRequest;
import com.translate.app.model.dto.request.TranslationUpdateRequest;
import com.translate.app.dao.entity.Translation;
import com.translate.app.model.exception.TranslationNotFoundException;
import com.translate.app.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/translate")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<String> addTranslation(@RequestBody TranslationAddRequest translationRequest) {
        if (isInvalidAddRequest(translationRequest)) {
            return ResponseEntity.badRequest().body("All fields are required.");
        }

        Translation translation = createTranslationFromAddRequest(translationRequest);

        translationService.addTranslation(translation);

        return ResponseEntity.status(HttpStatus.CREATED).body("Translation added successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTranslation(
            @PathVariable Integer id,
            @RequestBody TranslationUpdateRequest translationRequest) {

        if (isInvalidUpdateRequest(translationRequest)) {
            return ResponseEntity.badRequest().body("All fields are required.");
        }

        Translation updatedTranslation = createTranslationFromUpdateRequest(translationRequest);

        try {
            translationService.updateTranslation(id, updatedTranslation);
            return ResponseEntity.ok("Translation updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> translate(@RequestParam String sourceLang,
                                            @RequestParam String targetLang,
                                            @RequestParam String sourceText) {
        String translatedText = translationService.translate(sourceLang, targetLang, sourceText);
        return ResponseEntity.ok(translatedText);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTranslation(@PathVariable Integer id) {
        try {
            translationService.deleteTranslation(id);
            return ResponseEntity.ok("Translation deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likeTranslation(@PathVariable Integer id) {
        try {
            translationService.likeTranslation(id);
            return ResponseEntity.ok("Translation liked successfully.");
        } catch (TranslationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<String> dislikeTranslation(@PathVariable Integer id) {
        try {
            translationService.dislikeTranslation(id);
            return ResponseEntity.ok("Translation unliked successfully.");
        } catch (TranslationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> findTranslationById(@PathVariable Integer id) {
        try {
            translationService.findTranslationById(id);
            return ResponseEntity.ok("Translation viewed successfully.");
        } catch (TranslationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/addToFavorites")
    public ResponseEntity<String> addToFavorites(@PathVariable Integer id) {
        try {
            translationService.addToFavorites(id);
            return ResponseEntity.ok("Translation added to favorites successfully.");
        } catch (TranslationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/favorites")
    public Set<Translation> getFavoriteTranslations() {
        User user = translationService.getCurrentUser();
        return translationService.getFavoriteTranslations(user);
    }

    // Additional methods

    private boolean isInvalidUpdateRequest(TranslationUpdateRequest request) {
        return request.getSourceLanguage() == null ||
                request.getTargetLanguage() == null ||
                request.getSourceText() == null ||
                request.getTargetText() == null;
    }

    private boolean isInvalidAddRequest(TranslationAddRequest request) {
        return request.getSourceLanguage() == null ||
                request.getTargetLanguage() == null ||
                request.getSourceText() == null ||
                request.getTargetText() == null;
    }

    // Utility method to create Translation entity from request
    private Translation createTranslationFromUpdateRequest(TranslationUpdateRequest request) {
        return new Translation(
                request.getSourceLanguage(),
                request.getTargetLanguage(),
                request.getSourceText(),
                request.getTargetText()
        );
    }

    private Translation createTranslationFromAddRequest(TranslationAddRequest request) {
        return new Translation(
                request.getSourceLanguage(),
                request.getTargetLanguage(),
                request.getSourceText(),
                request.getTargetText()
        );
    }
}