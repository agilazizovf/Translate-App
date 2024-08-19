package com.translate.app.controller;

import com.translate.app.model.dto.request.PhraseBookAddRequest;
import com.translate.app.service.PhraseBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phrase-books")
@RequiredArgsConstructor
public class PhraseBookController {

    private final PhraseBookService phraseBookService;

    @PostMapping
    public PhraseBookAddRequest createPhraseBook(@RequestBody PhraseBookAddRequest phraseBookDTO) {
        return phraseBookService.createPhraseBook(phraseBookDTO);
    }

    @GetMapping
    public List<PhraseBookAddRequest> getAllPhraseBooks() {
        return phraseBookService.getAllPhraseBooks();
    }

    @GetMapping("/{id}")
    public PhraseBookAddRequest getPhraseBookById(@PathVariable Integer id) {
        return phraseBookService.getPhraseBookById(id);
    }
}
