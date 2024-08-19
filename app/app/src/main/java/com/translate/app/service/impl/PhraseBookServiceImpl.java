package com.translate.app.service.impl;

import com.translate.app.dao.entity.PhraseBook;
import com.translate.app.dao.repository.CategoryRepository;
import com.translate.app.dao.repository.PhraseBookRepository;
import com.translate.app.model.dto.request.PhraseBookAddRequest;
import com.translate.app.service.PhraseBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhraseBookServiceImpl implements PhraseBookService {

    private final PhraseBookRepository phraseBookRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public PhraseBookAddRequest createPhraseBook(PhraseBookAddRequest phraseBookAddRequest) {
        PhraseBook phraseBook = new PhraseBook();
        phraseBook.setName(phraseBookAddRequest.getName());
        phraseBook.setCategory(categoryRepository.findById(phraseBookAddRequest.getCategoryId()).orElse(null));
        return convertToDTO(phraseBookRepository.save(phraseBook));
    }

    @Override
    public List<PhraseBookAddRequest> getAllPhraseBooks() {
        return phraseBookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhraseBookAddRequest getPhraseBookById(Integer id) {
        return phraseBookRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private PhraseBookAddRequest convertToDTO(PhraseBook phraseBook) {
        PhraseBookAddRequest dto = new PhraseBookAddRequest();
        dto.setId(phraseBook.getId());
        dto.setName(phraseBook.getName());
        dto.setCategoryId(phraseBook.getCategory().getId());
        return dto;
    }
}
