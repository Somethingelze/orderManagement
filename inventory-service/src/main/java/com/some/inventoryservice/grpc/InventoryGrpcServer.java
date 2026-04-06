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
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void checkAvailability(ProductRequestDto request, StreamObserver<ProductResponseDto> responseObserver) {

        Map<String, Long> requestedProducts = request.getOrderItemsMap();
        List<OrderItemDto> orderItems = new ArrayList<>();

        log.info("Receive order {} in inventory service", request.getOrderId());

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

            OrderItemDto orderItem = OrderItemDto.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setProductId(product.getId())
                    .setOrderId(request.getOrderId())
                    .setProductName(product.getName())
                    .setPricePennies(priceInPennies)
                    .setSalePennies(saleInPennies)
                    .setTotalPrice((priceInPennies - saleInPennies) * requestedQuantity)
                    .setIsAvailable(isAvailable)
                    .build();

            product.setQuantity(product.getQuantity() - requestedQuantity);

            orderItems.add(orderItem);
            log.info("Product with id " + orderItem.getProductId() + " and name " + orderItem.getProductName() + " has been added to order items");
        });

        ProductResponseDto productResponseDto = ProductResponseDto.newBuilder()
                .addAllItems(orderItems)
                .build();

        responseObserver.onNext(productResponseDto);
        responseObserver.onCompleted();

        List<String> productsId = orderItems.stream()
                .map(OrderItemDto::getProductId)
                .toList();

        log.info("Order items {} has been added to order {} in inventory service and sending to order service", productsId, request.getOrderId());
    }
}
