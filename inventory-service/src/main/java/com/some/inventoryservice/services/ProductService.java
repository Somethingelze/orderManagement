package com.some.inventoryservice.services;

import com.google.protobuf.Empty;
import com.some.grpc.inventory.AvailabilityProductsDto;
import com.some.grpc.inventory.ConfirmedOrderId;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.model.entities.ReservedItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;


public interface ProductService {


    ReservedItemEntity reserveProductsInInventory(ProductEntity productEntity, Long requestedQuantity, String orderId);

    long convertToPennies(BigDecimal value);

    ProductEntity createProduct(ProductEntity productEntity);

    ProductEntity getProductById(UUID id);

    Page<ProductEntity> getAllProducts(Pageable pageable);

    ProductEntity updateProduct(UUID id, ProductEntity productEntity);

    void deleteProduct(UUID id);

    AvailabilityProductsDto checkAvailability(ProductRequestDto request);

    Empty confirmOrder(ConfirmedOrderId request);

    Empty cancelConfirmation(ConfirmedOrderId request);

    ProductResponseDto collectItems(AvailabilityProductsDto availabilityProductsDto);
}
