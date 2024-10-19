package com.forever.services;

import com.forever.dtos.CategoryDto;
import com.forever.dtos.CategoryTypeDto;
import com.forever.entities.Category;
import com.forever.entities.CategoryType;
import com.forever.exceptions.ResourceNotFoundException;
import com.forever.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElse(null);
    }

    public Category createCategory(CategoryDto categoryDto) {
        Category category = mapToCategory(categoryDto);
        return categoryRepository.save(category);
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(UUID id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with Id " + id));

        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        if (categoryDto.getCode() != null) {
            category.setCode(categoryDto.getCode());
        }
        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
        }

        List<CategoryType> existingList = category.getCategoryTypes();
        List<CategoryType> newList = new ArrayList<>();

        if (categoryDto.getCategoryTypes() != null) {
            categoryDto.getCategoryTypes().forEach(categoryTypeDto -> {
                if (categoryTypeDto.getId() != null) {
                    Optional<CategoryType> optionalCategoryType = existingList.stream()
                            .filter(t -> t.getId().equals(categoryTypeDto.getId()))
                            .findFirst();

                    optionalCategoryType.ifPresentOrElse(categoryType -> {
                        categoryType.setCode(categoryTypeDto.getCode());
                        categoryType.setName(categoryTypeDto.getName());
                        categoryType.setDescription(categoryTypeDto.getDescription());
                        newList.add(categoryType);
                    }, () -> {
                        throw new ResourceNotFoundException("CategoryType not found with Id " + categoryTypeDto.getId());
                    });
                } else {
                    CategoryType categoryType = new CategoryType();
                    categoryType.setCode(categoryTypeDto.getCode());
                    categoryType.setName(categoryTypeDto.getName());
                    categoryType.setDescription(categoryTypeDto.getDescription());
                    categoryType.setCategory(category);
                    newList.add(categoryType);
                }
            });
        }
        category.setCategoryTypes(newList);

        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    private Category mapToCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .code(categoryDto.getCode())
                .description(categoryDto.getDescription())
                .build();

        if (categoryDto.getCategoryTypes() != null) {
            List<CategoryType> categoryTypes = mapToCategoryTypeList(categoryDto.getCategoryTypes(), category);
            category.setCategoryTypes(categoryTypes);
        }

        return category;
    }

    private List<CategoryType> mapToCategoryTypeList(List<CategoryTypeDto> categoryTypes, Category category) {
        return categoryTypes.stream().map(categoryTypeDto -> CategoryType.builder()
                .name(categoryTypeDto.getName())
                .code(categoryTypeDto.getCode())
                .description(categoryTypeDto.getDescription())
                .category(category)
                .build()).collect(Collectors.toList());
    }

}
