package com.translate.app.controller;

import com.translate.app.dto.request.TranslationAddRequest;
import com.translate.app.dto.request.TranslationUpdateRequest;
import com.translate.app.entity.Translation;
import com.translate.app.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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