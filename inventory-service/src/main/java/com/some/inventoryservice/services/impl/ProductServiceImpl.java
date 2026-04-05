package com.some.inventoryservice.services.impl;

import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductEntity createProduct(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }

    @Override
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + "not found"));
    }

    @Override
    public Page<ProductEntity> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public ProductEntity updateProduct(String id, ProductEntity productEntity) {
        return productRepository.findById(id)
                .map(newProduct ->{
                    newProduct.setName(productEntity.getName());
                    newProduct.setPrice(productEntity.getPrice());
                    newProduct.setQuantity(productEntity.getQuantity());
                    productRepository.save(newProduct);
                    return newProduct;
                })
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + "not found"));
    }

    @Override
    public void deleteProduct(UUID id)    {
        productRepository.deleteById(id);
    }

}
