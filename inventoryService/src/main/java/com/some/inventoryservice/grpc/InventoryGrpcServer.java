package com.some.inventoryservice.grpc;

import com.some.grpc.inventory.InventoryServiceGrpc;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.inventoryservice.exceptions.ProductNotEnoughException;
import com.some.inventoryservice.exceptions.ProductNotFindException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.repository.ProductRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductRepository productRepository;

    public InventoryGrpcServer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void checkAvailability(ProductRequestDto request, StreamObserver<ProductResponseDto> responseObserver) {
        ProductEntity productEntity = productRepository.findById(Long.valueOf(request.getId()))
                .orElseThrow(() -> new ProductNotFindException("Product with id: " + request.getId() + "not found"));

        long priceInPennies = productEntity.getPrice()
                .movePointRight(2)
                .longValue();

        long saleInPennies = productEntity.getSale()
                .movePointRight(2)
                .longValue();

        if (request.getQuantity() > productEntity.getQuantity())    {
            throw new ProductNotEnoughException("Quantity of product with id " + request.getId() + "is not enough");
        }

        ProductResponseDto response = ProductResponseDto.newBuilder()
                .setId(productEntity.getId().toString())
                .setName(productEntity.getName())
                .setQuantity(productEntity.getQuantity())
                .setPricePennies(priceInPennies)
                .setSalePennies(saleInPennies)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
