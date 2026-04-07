package com.some.inventoryservice.services;

import com.some.grpc.inventory.OrderItemDto;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.inventoryservice.model.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductEntity createProduct(ProductEntity productEntity);

    ProductEntity getProductById(Long id);

    Page<ProductEntity> getAllProducts(Pageable pageable);

    ProductEntity updateProduct(String id, ProductEntity productEntity);

    void deleteProduct(UUID id);

    ProductResponseDto collectItems(ProductRequestDto request);
}
