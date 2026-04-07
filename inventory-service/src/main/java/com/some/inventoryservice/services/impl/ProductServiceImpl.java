package com.some.inventoryservice.services.impl;

import com.some.grpc.inventory.OrderItemDto;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.inventoryservice.annotations.Loggable;
import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Loggable
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

    @Override
    @Transactional
    public ProductResponseDto collectItems(ProductRequestDto request) {

        Map<String, Long> requestedProducts = request.getOrderItemsMap();
        List<OrderItemDto> orderItems = new ArrayList<>();

        requestedProducts.forEach((id, requestedQuantity) -> {
            ProductEntity product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

            boolean isAvailable = requestedQuantity < product.getQuantity();
            long priceInPennies = product.getPrice()
                    .movePointRight(2)
                    .longValue();
            long saleInPennies = product.getSale()
                    .movePointRight(2)
                    .longValue();
            long totalPrice = (priceInPennies - saleInPennies) * requestedQuantity;

            OrderItemDto orderItem = OrderItemDto.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setProductId(product.getId())
                    .setOrderId(request.getOrderId())
                    .setProductName(product.getName())
                    .setPricePennies(priceInPennies)
                    .setSalePennies(saleInPennies)
                    .setTotalPrice(totalPrice)
                    .setIsAvailable(isAvailable)
                    .build();

            if(isAvailable) {
                product.setQuantity(product.getQuantity() - requestedQuantity);
            }

            orderItems.add(orderItem);
        });

        return ProductResponseDto.newBuilder()
                .addAllItems(orderItems)
                .build();
    }

}
