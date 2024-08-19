package com.translate.app.service;

import com.translate.app.model.dto.request.PhraseAddRequest;

import java.util.List;

public interface PhraseService {

    PhraseAddRequest createPhrase(PhraseAddRequest phraseDTO);
    List<PhraseAddRequest> getAllPhrases();
    PhraseAddRequest getPhraseById(Integer id);
}
