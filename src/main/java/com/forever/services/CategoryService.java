package com.forever.services;

import com.forever.dtos.CategoryDto;
import com.forever.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    Category getCategory(UUID id);
    Category createCategory(CategoryDto categoryDto);
    List<Category> getCategories();
    Category updateCategory(UUID id, CategoryDto categoryDto);
    void deleteCategory(UUID id);
}
