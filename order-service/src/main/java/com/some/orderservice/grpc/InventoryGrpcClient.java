package com.some.orderservice.grpc;

import com.some.grpc.inventory.*;

import com.some.orderservice.exceptions.InventoryServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryGrpcClient {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub syncStub;

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackAvailability")
    public AvailabilityProductsDto checkAvailability(ProductRequestDto request) {
        try {
            return syncStub.checkAvailability(request);
        } catch (Exception e) {
            throw new InventoryServiceUnavailableException("RPC failed: " + e.getMessage());
        }
    }

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackCollect")
    public ProductResponseDto collectOrder(AvailabilityProductsDto request) {
        try {
            return syncStub.collectOrder(request);
        } catch (Exception e) {
            throw new InventoryServiceUnavailableException("RPC failed: " + e.getMessage());
        }
    }

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackVoid")
    public void confirmOrder(ConfirmedOrderId confirmedOrderId) {
        try {
            syncStub.confirmOrder(confirmedOrderId);
        } catch (Exception e) {
            throw new InventoryServiceUnavailableException("RPC failed: " + e.getMessage());
        }
    }

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackVoid")
    public void cancelConfirmation(ConfirmedOrderId confirmedOrderId) {
        try {
            syncStub.cancelConfirmation(confirmedOrderId);
        } catch (Exception e) {
            throw new InventoryServiceUnavailableException("RPC failed: " + e.getMessage());
        }
    }

    public AvailabilityProductsDto fallbackAvailability(ProductRequestDto request, Throwable t) {
        log.error("InventoryGrpcClient fallbackAvailability", t);
        return AvailabilityProductsDto.newBuilder()
                .setIsAvailable(false)
                .build();

    }

    private ProductResponseDto fallbackCollect(AvailabilityProductsDto request, Throwable t) {
        log.error("InventoryGrpcClient fallbackCollect", t);
        return ProductResponseDto.newBuilder()
                .build();
    }

    private void fallbackVoid(ConfirmedOrderId confirmedOrderId, Throwable t) {
        log.error("InventoryGrpcClient fallbackVoid", t);
    }
}