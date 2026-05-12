package com.some.inventoryservice.services.impl;

import com.google.protobuf.Empty;
import com.some.commonlib.annotations.Loggable;
import com.some.grpc.inventory.*;
import com.some.inventoryservice.exceptions.OrderIdNotFoundException;
import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.model.redisHash.redisHash.RedisReservation;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.repository.ReservationProductsRepository;
import com.some.inventoryservice.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Loggable
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ReservationProductsRepository reservationProductsRepository;

    @Override
    public AvailabilityProductsDto checkAvailability(ProductRequestDto request) {

        Map<String, Long> requestedProducts = request.getOrderItemsMap();
        List<UUID> requestedIds = requestedProducts.keySet().stream()
                .map(UUID::fromString)
                .toList();
        Map<UUID, ProductEntity> existedProducts = productRepository.findAllById(requestedIds).stream()
                .collect(Collectors.toMap(ProductEntity::getId,
                        product -> product));

        List<String> missedProductsIds = new ArrayList<>();
        Map<String, Long> availableProducts = new HashMap<>();

        requestedProducts.forEach((productId, quantity) -> {
            ProductEntity product = existedProducts.get(UUID.fromString(productId));
            if (product != null && product.getQuantity() >= quantity) {
                availableProducts.put(productId, quantity);
            } else {
                missedProductsIds.add(productId);
            }
        });

        return AvailabilityProductsDto.newBuilder()
                .setOrderId(request.getOrderId())
                .setIsAvailable(missedProductsIds.isEmpty())
                .putAllAvailableProducts(availableProducts)
                .addAllUnavailableProducts(missedProductsIds)
                .build();
    }

    @Override
    @Transactional
    public ProductResponseDto collectItems(AvailabilityProductsDto availabilityProductsDto) {
        String orderId = availabilityProductsDto.getOrderId();
        Map<String, Long> availableProducts = availabilityProductsDto.getAvailableProductsMap();
        List<ProductEntity> products = productRepository.findAllById(availableProducts.keySet()
                .stream()
                .map(UUID::fromString)
                .toList());

        reserve(availableProducts, products, orderId);

        List<OrderItemDto> orderItems = new ArrayList<>();

        products.forEach(product -> {
            long price = convertToPennies(product.getPrice());
            long sale = convertToPennies(product.getSale());
            long requestedQuantity = availableProducts.get(String.valueOf(product.getId()));

            long totalPrice = (price - sale) * requestedQuantity;

            OrderItemDto orderItem = OrderItemDto.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setProductId(product.getId().toString())
                    .setProductName(product.getName())
                    .setPricePennies(price)
                    .setSalePennies(sale)
                    .setTotalPrice(totalPrice)
                    .build();

            orderItems.add(orderItem);
        });

        return ProductResponseDto.newBuilder()
                .addAllItems(orderItems)
                .build();
    }

    @Override
    @Transactional
    public Empty confirmOrder(ConfirmedOrderId request) {
        RedisReservation redisReservation = reservationProductsRepository.findById(request.getId())
                .orElseThrow(() -> new OrderIdNotFoundException("Reservation not found or expired"));

        Map<String, Long> reservedProducts = redisReservation.getReservedProducts();
        List<ProductEntity> products = productRepository.findAllById(reservedProducts.keySet()
                .stream()
                .map(UUID::fromString)
                .toList()
        );

        products.forEach(product -> {
            product.setReservedQuantity(product.getReservedQuantity() - reservedProducts.get(product.getId().toString()));
        });

        reservationProductsRepository.deleteById(request.getId());
        return Empty.getDefaultInstance();
    }

    @Override
    @Transactional
    public Empty cancelConfirmation(ConfirmedOrderId request) {
        RedisReservation redisReservation = reservationProductsRepository.findById(request.getId())
                .orElseThrow(() -> new OrderIdNotFoundException("Reservation not found or expired"));
        Map<String, Long>  reservedProducts = redisReservation.getReservedProducts();

        List<ProductEntity> products = productRepository.findAllById(reservedProducts.keySet()
                .stream()
                .map(UUID::fromString)
                .toList());

        products.forEach(product -> {
            Long quantity = reservedProducts.get(product.getId().toString());
            product.setQuantity(product.getQuantity() + quantity);
            product.setReservedQuantity(product.getReservedQuantity() - quantity);
        });

        reservationProductsRepository.deleteById(request.getId());
        return Empty.getDefaultInstance();
    }


    @Override
    @Transactional
    public RedisReservation reserve(Map<String, Long> reservedProducts, List<ProductEntity> products, String orderId) {
        if (reservationProductsRepository.existsById(orderId))    {
            log.debug("Duplicate request for order {}. Returning existing reservation.", orderId);
            return reservationProductsRepository.findById(orderId).orElseThrow(() ->
                    new OrderIdNotFoundException("Order id not found."));
        }

        products.forEach(product -> {
            product.setQuantity(product.getQuantity() - reservedProducts.get(String.valueOf(product.getId())));
            product.setReservedQuantity(product.getReservedQuantity() + reservedProducts.get(String.valueOf(product.getId())));
        });

        RedisReservation redisReservations = RedisReservation.builder()
                .orderId(orderId)
                .reservedProducts(reservedProducts)
                .build();

        log.debug("Saving reservation to Redis for order: {}", orderId);
        return reservationProductsRepository.save(redisReservations);
    }

    @Override
    public long convertToPennies(BigDecimal value) {
        return value.movePointRight(2)
                .longValue();
    }

    @Override
    public ProductEntity createProduct(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }

    @Override
    public ProductEntity getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + "not found"));
    }

    @Override
    public Page<ProductEntity> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public ProductEntity updateProduct(UUID id, ProductEntity productEntity) {
        return productRepository.findById(id)
                .map(newProduct -> {
                    newProduct.setName(productEntity.getName());
                    newProduct.setPrice(productEntity.getPrice());
                    newProduct.setSale(productEntity.getSale());
                    newProduct.setQuantity(productEntity.getQuantity());
                    newProduct.setReservedQuantity(productEntity.getReservedQuantity());
                    return newProduct;
                })
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + "not found"));
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

}
