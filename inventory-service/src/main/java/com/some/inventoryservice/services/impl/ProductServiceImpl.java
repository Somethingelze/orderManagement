package com.some.inventoryservice.services.impl;

import com.google.protobuf.Empty;
import com.some.commonlib.annotations.Loggable;
import com.some.grpc.inventory.*;
import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.model.entities.ReservedItemEntity;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.repository.ReservedItemRepository;
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
    private final ReservedItemRepository reservedItemRepository;

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

        //

//        List<String> missedProductsIds = requestedProducts.entrySet().stream()
//                .filter(entry -> {
//                    UUID productId = UUID.fromString(entry.getKey());
//                    long requestedQty = entry.getValue();
//                    return existedProducts.stream()
//                            .filter(p -> p.getId().equals(productId))
//                            .findFirst()
//                            .map(p -> p.getQuantity() < requestedQty)
//                            .orElse(true);
//                })
//                .map(Map.Entry::getKey)
//                .toList();

//        List<ProductEntity> availableToReserve = existedProducts.stream()
//                .filter(p -> !missedProductsIds.contains(p.getId().toString()))
//                .toList();


//        Map<String, Long> availabilityMap = availableToReserve.stream()
//                .collect(Collectors.toMap(
//                        p -> p.getId().toString(),
//                        p -> requestedProducts.get(p.getId().toString())
//                ));

    }

    @Override
    @Transactional
    public ProductResponseDto collectItems(AvailabilityProductsDto availabilityProductsDto) {
        if (reservedItemRepository.existsByOrderId(UUID.fromString(availabilityProductsDto.getOrderId()))) {
            log.debug("Duplicate request for order {}. Returning existing reservation.", availabilityProductsDto.getOrderId());
        }

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
                    .setPricePennies(convertToPennies(product.getPrice()))
                    .setSalePennies(convertToPennies(product.getSale()))
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

        UUID orderId = UUID.fromString(request.getId());
        List<ReservedItemEntity> reservedItemEntities = reservedItemRepository.findAllByOrderId(orderId);

        reservedItemEntities.forEach(item -> {
            ProductEntity product = item.getProduct();
            product.getReservedItemEntities().remove(item);
        });

        reservedItemRepository.deleteAll(reservedItemEntities);
        return Empty.getDefaultInstance();
    }

    @Override
    @Transactional
    public Empty cancelConfirmation(ConfirmedOrderId request) {
        UUID orderId = UUID.fromString(request.getId());
        List<ReservedItemEntity> reservedItemEntities = reservedItemRepository.findAllByOrderId(orderId);

        reservedItemEntities.forEach(item -> {
            ProductEntity product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            product.getReservedItemEntities().remove(item);
        });

        reservedItemRepository.deleteAll(reservedItemEntities);
        return Empty.getDefaultInstance();
    }

    @Transactional
    @Override
    public List<ReservedItemEntity> reserve(Map<String, Long> availableProducts, List<ProductEntity> products, String orderId) {
        List<ReservedItemEntity> reservedProducts = products.stream()
                .map(product -> {
                    String productId = String.valueOf(product.getId());
                    product.setQuantity(product.getQuantity() - availableProducts.get(productId));
                    return ReservedItemEntity.builder()
                            .id(UUID.randomUUID())
                            .product(product)
                            .quantity(availableProducts.get(productId))
                            .orderId(UUID.fromString(orderId))
                            .build();
                })
                .toList();
        return reservedItemRepository.saveAll(reservedProducts);
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
                    newProduct.setReservedItemEntities(productEntity.getReservedItemEntities());
                    productRepository.save(newProduct);
                    return newProduct;
                })
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + "not found"));
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

}
