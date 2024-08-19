package com.translate.app.service;

import com.translate.app.model.dto.request.CategoryAddRequest;

import java.util.List;

public interface CategoryService {

    CategoryAddRequest createCategory(CategoryAddRequest categoryAddRequest);
    List<CategoryAddRequest> getAllCategories();
    CategoryAddRequest getCategoryById(Integer id);
}
