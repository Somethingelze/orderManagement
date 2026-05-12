package com.some.inventoryservice.grpc;

import com.google.protobuf.Empty;
import com.some.commonlib.annotations.Loggable;
import com.some.grpc.inventory.*;
import com.some.inventoryservice.services.ProductService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;


@Slf4j
@GrpcService
@RequiredArgsConstructor
@Loggable
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductService productService;

    @Override
    public void checkAvailability(ProductRequestDto request,
                                  StreamObserver<AvailabilityProductsDto> responseObserver) {
            responseObserver.onNext(productService.checkAvailability(request));
            responseObserver.onCompleted();

    }

    @Override
    public void collectOrder(AvailabilityProductsDto request,
                             StreamObserver<ProductResponseDto> responseObserver) {
        responseObserver.onNext(productService.collectItems(request));
        responseObserver.onCompleted();
    }

    @Override
    public void confirmOrder(ConfirmedOrderId request,
                             StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(productService.confirmOrder(request));
        responseObserver.onCompleted();
    }

    @Override
    public void cancelConfirmation (ConfirmedOrderId request,
                             StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(productService.cancelConfirmation(request));
        responseObserver.onCompleted();
    }
}
