package com.translate.app.service.impl;

import com.translate.app.dao.entity.Phrase;
import com.translate.app.dao.repository.PhraseBookRepository;
import com.translate.app.dao.repository.PhraseRepository;
import com.translate.app.model.dto.request.PhraseAddRequest;
import com.translate.app.service.PhraseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhraseServiceImpl implements PhraseService {

    private final PhraseRepository phraseRepository;
    private final PhraseBookRepository phraseBookRepository;

    @Override
    public PhraseAddRequest createPhrase(PhraseAddRequest phraseDTO) {
        Phrase phrase = new Phrase();
        phrase.setContent(phraseDTO.getContent());
        phrase.setPhraseBook(phraseBookRepository.findById(phraseDTO.getPhraseBookId()).orElse(null));
        return convertToDTO(phraseRepository.save(phrase));
    }

    @Override
    public List<PhraseAddRequest> getAllPhrases() {
        return phraseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhraseAddRequest getPhraseById(Integer id) {
        return phraseRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private PhraseAddRequest convertToDTO(Phrase phrase) {
        PhraseAddRequest dto = new PhraseAddRequest();
        dto.setId(phrase.getId());
        dto.setContent(phrase.getContent());
        dto.setPhraseBookId(phrase.getPhraseBook().getId());
        return dto;
    }
}
