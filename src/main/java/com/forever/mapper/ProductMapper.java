package com.forever.mapper;

import com.forever.dtos.ProductDto;
import com.forever.dtos.ProductVariantDto;
import com.forever.dtos.ResourceDto;
import com.forever.entities.*;
import com.forever.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryService categoryService;

    public Product mapToProduct(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .brand(productDto.getBrand())
                .rating(productDto.getRating())
                .discount(productDto.getDiscount())
                .slug(productDto.getSlug())
                .isNewArrival(productDto.getIsNewArrival())
                .build();

        if (productDto.getId() != null) {
            product.setId(productDto.getId());
        }

        Category category = categoryService.getCategory(productDto.getCategoryId());
        if (category != null) {
            product.setCategory(category);
            UUID categoryTypeId = productDto.getCategoryTypeId();

            CategoryType categoryType = category.getCategoryTypes().stream().filter(categoryType1 ->
                    categoryType1.getId().equals(categoryTypeId)).findFirst().orElse(null);
            product.setCategoryType(categoryType);
        }

        if (productDto.getProductVariants() != null) {
            product.setProductVariants(mapToProductVariantList(productDto.getProductVariants(), product));
        }

        if (productDto.getResources() != null) {
            product.setResources(mapToResourceList(productDto.getResources(), product));
        }

        return product;
    }

    private List<ProductVariant> mapToProductVariantList(List<ProductVariantDto> productVariantDtos, Product product) {
        return productVariantDtos.stream().map(productVariantDto -> {
            ProductVariant productVariant = ProductVariant.builder()
                    .color(productVariantDto.getColor())
                    .size(productVariantDto.getSize())
                    .stockQuantity(productVariantDto.getStockQuantity())
                    .product(product)
                    .build();

            if (productVariantDto.getId() != null) {
                productVariant.setId(productVariantDto.getId());
            }

            return productVariant;
        }).collect(Collectors.toList());
    }

    private List<Resource> mapToResourceList(List<ResourceDto> resourceDtos, Product product) {
        return resourceDtos.stream().map(resourceDto -> {
            Resource resource = Resource.builder()
                    .name(resourceDto.getName())
                    .url(resourceDto.getUrl())
                    .type(resourceDto.getType())
                    .isPrimary(resourceDto.getIsPrimary())
                    .product(product)
                    .build();

            if (resourceDto.getId() != null) {
                resource.setId(resourceDto.getId());
            }

            return resource;
        }).collect(Collectors.toList());
    }

    public List<ProductDto> mapToProductDtoList(List<Product> products) {
        return products.stream().map(this::mapToProductDto).toList();
    }

    public ProductDto mapToProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .rating(product.getRating())
                .discount(product.getDiscount())
                .slug(product.getSlug())
                .isNewArrival(product.getIsNewArrival())
                .description(product.getDescription())
                .thumbnail(getProductThumbnail(product.getResources()))
                .build();
    }

    private String getProductThumbnail(List<Resource> resources) {
        return Objects.requireNonNull(resources.stream().filter(Resource::getIsPrimary)
                .findFirst().orElse(null)).getUrl();
    }

    public List<ProductVariantDto> maptToProductVariantDtoList(List<ProductVariant> productVariants) {
        return productVariants.stream().map(this::mapToProductVariantDto).toList();
    }

    private ProductVariantDto mapToProductVariantDto(ProductVariant productVariant) {
        return ProductVariantDto.builder()
                .id(productVariant.getId())
                .size(productVariant.getSize())
                .color(productVariant.getColor())
                .stockQuantity(productVariant.getStockQuantity())
                .build();
    }

    public List<ResourceDto> mapToResouceDtoList(List<Resource> resources) {
        return resources.stream().map(this::mapToResourceDto).toList();
    }

    private ResourceDto mapToResourceDto(Resource resource) {
        return ResourceDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .url(resource.getUrl())
                .isPrimary(resource.getIsPrimary())
                .type(resource.getType())
                .build();
    }
}
