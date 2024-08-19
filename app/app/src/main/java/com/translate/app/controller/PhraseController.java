package com.translate.app.controller;

import com.translate.app.model.dto.request.PhraseAddRequest;
import com.translate.app.service.PhraseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phrases")
@RequiredArgsConstructor
public class PhraseController {

    private final PhraseService phraseService;

    @PostMapping
    public PhraseAddRequest createPhrase(@RequestBody PhraseAddRequest phraseDTO) {
        return phraseService.createPhrase(phraseDTO);
    }

    @GetMapping
    public List<PhraseAddRequest> getAllPhrases() {
        return phraseService.getAllPhrases();
    }
}
