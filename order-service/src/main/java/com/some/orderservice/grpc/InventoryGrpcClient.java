package com.some.orderservice.grpc;

import com.some.grpc.inventory.InventoryServiceGrpc;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;

import com.some.orderservice.exceptions.InventoryServiceUnavailableException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;

@Service
@RequiredArgsConstructor
public class InventoryGrpcClient {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub syncStub;

    public ProductResponseDto checkAvailability(ProductRequestDto request) {
        try {
            return syncStub.checkAvailability(request);
        } catch (Exception e) {
            throw new InventoryServiceUnavailableException("RPC failed: " + e.getMessage());
        }
    }
}