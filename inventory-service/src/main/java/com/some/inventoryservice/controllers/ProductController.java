package com.some.inventoryservice.controllers;

import com.some.commonlib.annotations.Loggable;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Loggable
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductEntity>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok().body(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity productEntity) {
        return ResponseEntity.ok().body(productService.createProduct(productEntity));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(@PathVariable UUID id, @RequestBody ProductEntity productEntity) {
        return ResponseEntity.ok().body(productService.updateProduct(id, productEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
