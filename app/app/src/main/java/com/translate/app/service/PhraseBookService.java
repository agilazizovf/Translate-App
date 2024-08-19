package com.translate.app.service;

import com.translate.app.model.dto.request.PhraseBookAddRequest;

import java.util.List;

public interface PhraseBookService {

    PhraseBookAddRequest createPhraseBook(PhraseBookAddRequest phraseBookAddRequest);
    List<PhraseBookAddRequest> getAllPhraseBooks();
    PhraseBookAddRequest getPhraseBookById(Integer id);
}
