package com.translate.app.controller;

import com.translate.app.model.dto.request.CategoryAddRequest;
import com.translate.app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryAddRequest createCategory(@RequestBody CategoryAddRequest categoryDTO) {
        return categoryService.createCategory(categoryDTO);
    }

    @GetMapping
    public List<CategoryAddRequest> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryAddRequest getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }
}
