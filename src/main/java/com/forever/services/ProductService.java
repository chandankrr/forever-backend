package com.forever.services;

import com.forever.dtos.ProductDto;
import com.forever.entities.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addProduct(ProductDto productDto);
    List<Product> getAllProducts(UUID categoryId, UUID categoryTypeId);
}
