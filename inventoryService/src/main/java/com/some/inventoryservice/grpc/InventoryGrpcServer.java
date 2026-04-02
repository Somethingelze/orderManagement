package com.some.inventoryservice.grpc;

import com.some.grpc.inventory.InventoryServiceGrpc;
import com.some.grpc.inventory.OrderItemDto;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.inventoryservice.exceptions.ProductNotEnoughException;
import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.repository.ProductRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void checkAvailability(ProductRequestDto request, StreamObserver<ProductResponseDto> responseObserver) {
        Map<String, Long> requestedProducts = request.getOrderItemsMap();
        List<OrderItemDto> orderItems = new ArrayList<>();

        requestedProducts.forEach((id, requestedQuantity) -> {
            ProductEntity product = productRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

            if (product.getQuantity() < requestedQuantity) {
                throw new ProductNotEnoughException("Product with id " + id + " not enough");
            }

            long priceInPennies = product.getPrice()
                    .movePointRight(2)
                    .longValue();
            long saleInPennies = product.getSale()
                    .movePointRight(2)
                    .longValue();

            OrderItemDto orderItem = OrderItemDto.newBuilder()
                    .setProductId(product.getId())
                    .setProductName(product.getName())
                    .setPricePennies(priceInPennies)
                    .setSalePennies(saleInPennies)
                    .setTotalPrice(priceInPennies - saleInPennies * requestedQuantity)
                    .build();

            product.setQuantity(product.getQuantity() - requestedQuantity);

            orderItems.add(orderItem);
        });

        ProductResponseDto productResponseDto = ProductResponseDto.newBuilder()
                .addAllItems(orderItems)
                .build();

        responseObserver.onNext(productResponseDto);
        responseObserver.onCompleted();
    }
}
