package com.some.inventoryservice;

import com.some.grpc.inventory.AvailabilityProductsDto;
import com.some.grpc.inventory.ConfirmedOrderId;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.inventoryservice.exceptions.OrderIdNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.model.redisHash.redisHash.RedisReservation;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.repository.ReservationProductsRepository;
import com.some.inventoryservice.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservationProductsRepository reservationProductsRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private ProductEntity product;
    private String orderId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        orderId = UUID.randomUUID().toString();
        product = ProductEntity.builder()
                .id(productId)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .sale(new BigDecimal("10.00"))
                .quantity(50L)
                .reservedQuantity(0L)
                .build();
    }

    @Test
    void checkAvailability_Success() {
        ProductRequestDto request = ProductRequestDto.newBuilder()
                .setOrderId(orderId)
                .putOrderItems(productId.toString(), 5L)
                .build();

        Mockito.when(productRepository.findAllById(ArgumentMatchers.any())).thenReturn(List.of(product));

        AvailabilityProductsDto result = productService.checkAvailability(request);

        Assertions.assertTrue(result.getIsAvailable());
        Assertions.assertEquals(5L, result.getAvailableProductsMap().get(productId.toString()));
    }

    @Test
    void collectItems_ShouldUpdateQuantitiesAndSaveToRedis() {
        long requestedQty = 10L;
        AvailabilityProductsDto dto = AvailabilityProductsDto.newBuilder()
                .setOrderId(orderId)
                .putAvailableProducts(productId.toString(), requestedQty)
                .build();

        Mockito.when(productRepository.findAllById(ArgumentMatchers.any())).thenReturn(List.of(product));
        Mockito.when(reservationProductsRepository.existsById(orderId)).thenReturn(false);

        productService.collectItems(dto);

        Assertions.assertEquals(40L, product.getQuantity());
        Assertions.assertEquals(10L, product.getReservedQuantity());
        Mockito.verify(reservationProductsRepository, Mockito.times(1)).save(ArgumentMatchers.any(RedisReservation.class));
    }

    @Test
    void confirmOrder_ShouldReduceReservedAndCleanupRedis() {
        product.setQuantity(40L);
        product.setReservedQuantity(10L);
        ConfirmedOrderId request = ConfirmedOrderId.newBuilder().setId(orderId).build();
        RedisReservation reservation = RedisReservation.builder()
                .orderId(orderId)
                .reservedProducts(Map.of(productId.toString(), 10L))
                .build();

        Mockito.when(reservationProductsRepository.findById(orderId)).thenReturn(Optional.of(reservation));
        Mockito.when(productRepository.findAllById(ArgumentMatchers.any())).thenReturn(List.of(product));

        productService.confirmOrder(request);

        Assertions.assertEquals(0L, product.getReservedQuantity());
        Mockito.verify(reservationProductsRepository, Mockito.times(1)).deleteById(orderId);
    }

    @Test
    void confirmOrder_NotFound_ThrowsException() {
        Mockito.when(reservationProductsRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderIdNotFoundException.class, () ->
                productService.confirmOrder(ConfirmedOrderId.newBuilder().setId("fake_id").build())
        );
    }
}