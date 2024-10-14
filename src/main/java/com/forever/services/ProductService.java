package com.forever.services;

import com.forever.dtos.ProductDto;
import com.forever.entities.Product;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addProduct(ProductDto productDto);
    List<ProductDto> getAllProducts(UUID categoryId, UUID categoryTypeId);
    ProductDto getProductBySlug(String slug);
    ProductDto getProductById(UUID id);
    Product updateProduct(ProductDto productDto);
    Product fetchProductById(UUID id) throws BadRequestException;
}
