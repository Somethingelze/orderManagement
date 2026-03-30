package com.some.orderservice.grpc;

import com.some.grpc.inventory.InventoryServiceGrpc;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor // Добавь это
public class InventoryGrpcClient {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub syncStub;

    public ProductResponseDto checkAvailability(String id, Long quantity) {
        ProductRequestDto request = ProductRequestDto.newBuilder()
                .setId(id)
                .setQuantity(quantity)
                .build();

        try {
            return syncStub.checkAvailability(request);
        } catch (StatusRuntimeException e) {
            // Обработка ошибок: сервис недоступен или товар не найден
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Inventory Service error");
        }
    }
}