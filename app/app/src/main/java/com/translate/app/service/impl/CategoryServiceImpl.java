package com.translate.app.service.impl;

import com.translate.app.dao.entity.Category;
import com.translate.app.dao.repository.CategoryRepository;
import com.translate.app.model.dto.request.CategoryAddRequest;
import com.translate.app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Override
    public CategoryAddRequest createCategory(CategoryAddRequest categoryAddRequest) {
        Category category = new Category();
        category.setName(categoryAddRequest.getName());
        return convertToDTO(categoryRepository.save(category));    }

    @Override
    public List<CategoryAddRequest> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryAddRequest getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private CategoryAddRequest convertToDTO(Category category) {
        CategoryAddRequest dto = new CategoryAddRequest();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
