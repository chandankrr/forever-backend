package com.forever.services.impl;

import com.forever.dtos.ProductDto;
import com.forever.dtos.ProductVariantDto;
import com.forever.dtos.ResourceDto;
import com.forever.entities.*;
import com.forever.repositories.ProductRepository;
import com.forever.services.CategoryService;
import com.forever.services.ProductService;
import com.forever.utils.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Override
    public Product addProduct(ProductDto productDto) {
        Product product = mapToProduct(productDto);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts(UUID categoryId, UUID categoryTypeId) {
        Specification<Product> productSpecification = Specification.where(null);

        if (categoryId != null) {
            productSpecification = productSpecification.and(ProductSpecification.hasCategoryId(categoryId));
        }

        if (categoryTypeId != null) {
            productSpecification = productSpecification.and(ProductSpecification.hasCategoryTypeId(categoryTypeId));
        }

        return productRepository.findAll(productSpecification);
    }

    private Product mapToProduct(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .brand(productDto.getBrand())
                .rating(productDto.getRating())
                .isNewArrival(productDto.getIsNewArrival())
                .build();

        Category category = categoryService.getCategory(productDto.getCategoryId());
        if (category != null) {
            product.setCategory(category);
            UUID categoryTypeId = productDto.getCategoryTypeId();

            CategoryType categoryType = category.getCategoryTypes().stream().filter(categoryType1 ->
                    categoryType1.getId().equals(categoryTypeId)).findFirst().orElse(null);
            product.setCategoryType(categoryType);
        }

        if(productDto.getProductVariants() != null) {
            product.setProductVariants(mapToProductVariantList(productDto.getProductVariants(), product));
        }

        if(productDto.getResources() != null) {
            product.setResources(mapToResourceList(productDto.getResources(), product));
        }

        return productRepository.save(product);
    }

    private List<ProductVariant> mapToProductVariantList(List<ProductVariantDto> productVariantDtos, Product product) {
        return productVariantDtos.stream().map(productVariantDto -> ProductVariant.builder()
                .color(productVariantDto.getColor())
                .size(productVariantDto.getSize())
                .stockQuantity(productVariantDto.getStockQuantity())
                .product(product)
                .build()).collect(Collectors.toList());
    }

    private List<Resource> mapToResourceList(List<ResourceDto> resourceDtos, Product product) {
        return resourceDtos.stream().map(resourceDto -> Resource.builder()
                .name(resourceDto.getName())
                .url(resourceDto.getUrl())
                .type(resourceDto.getType())
                .isPrimary(resourceDto.getIsPrimary())
                .product(product)
                .build()).collect(Collectors.toList());
    }
}
