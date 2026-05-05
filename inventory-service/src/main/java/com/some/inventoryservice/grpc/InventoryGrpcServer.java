package com.some.inventoryservice.grpc;

import com.some.commonlib.annotations.Loggable;
import com.some.grpc.inventory.InventoryServiceGrpc;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.inventoryservice.services.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@GrpcService
@RequiredArgsConstructor
@Loggable
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductService productService;

    @Override
    @Transactional
    public void checkAvailability(ProductRequestDto request, StreamObserver<ProductResponseDto> responseObserver) {
        responseObserver.onNext(productService.collectItems(request));
        responseObserver.onCompleted();
    }
}
