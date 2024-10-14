package com.forever.services.impl;

import com.forever.dtos.ProductDto;
import com.forever.entities.Product;
import com.forever.exceptions.ResourceNotFoundException;
import com.forever.mapper.ProductMapper;
import com.forever.repositories.ProductRepository;
import com.forever.services.ProductService;
import com.forever.utils.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Product addProduct(ProductDto productDto) {
        Product product = productMapper.mapToProduct(productDto);
        return productRepository.save(product);
    }

    @Override
    public List<ProductDto> getAllProducts(UUID categoryId, UUID categoryTypeId) {
        Specification<Product> productSpecification = Specification.where(null);

        if (categoryId != null) {
            productSpecification = productSpecification.and(ProductSpecification.hasCategoryId(categoryId));
        }

        if (categoryTypeId != null) {
            productSpecification = productSpecification.and(ProductSpecification.hasCategoryTypeId(categoryTypeId));
        }

        List<Product> products = productRepository.findAll(productSpecification);

        return productMapper.mapToProductDtoList(products);
    }

    @Override
    public ProductDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug);

        if(product == null) {
            throw new ResourceNotFoundException("Product not found with slug: " + slug);
        }

        ProductDto productDto = productMapper.mapToProductDto(product);
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategoryTypeId(product.getCategoryType().getId());
        productDto.setProductVariants(productMapper.maptToProductVariantDtoList(product.getProductVariants()));
        productDto.setResources(productMapper.mapToResouceDtoList(product.getResources()));

        return productDto;
    }

    @Override
    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with id: " + id));

        ProductDto productDto = productMapper.mapToProductDto(product);
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategoryTypeId(product.getCategoryType().getId());
        productDto.setProductVariants(productMapper.maptToProductVariantDtoList(product.getProductVariants()));
        productDto.setResources(productMapper.mapToResouceDtoList(product.getResources()));

        return productDto;
    }

    @Override
    public Product updateProduct(ProductDto productDto) {
        productRepository.findById(productDto.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with id: " + productDto.getId()));
        return productRepository.save(productMapper.mapToProduct(productDto));
    }

    @Override
    public Product fetchProductById(UUID id) throws BadRequestException {
        return productRepository.findById(id).orElseThrow(BadRequestException::new);
    }

}
