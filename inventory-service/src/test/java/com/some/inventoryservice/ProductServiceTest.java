package com.some.inventoryservice;

import com.some.grpc.inventory.*;
import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.model.entities.ReservedItemEntity;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.repository.ReservedItemRepository;
import com.some.inventoryservice.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservedItemRepository reservedItemRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID orderId;
    private UUID productId;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();
        product = ProductEntity.builder()
                .id(productId)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .sale(new BigDecimal("10.00"))
                .quantity(50L)
                .reservedItemEntities(new ArrayList<>())
                .build();
    }

    @Test
    void checkAvailability_ShouldReturnExisting_WhenDuplicateRequest() {
        ReservedItemEntity existing = ReservedItemEntity.builder()
                .product(product)
                .quantity(5L)
                .build();

        when(reservedItemRepository.existsByOrderId(orderId)).thenReturn(true);
        when(reservedItemRepository.findAllByOrderId(orderId)).thenReturn(List.of(existing));

        ProductRequestDto request = ProductRequestDto.newBuilder()
                .setOrderId(orderId.toString())
                .build();

        AvailabilityProductsDto result = productService.checkAvailability(request);

        assertTrue(result.getIsAvailable());
        assertEquals(5L, result.getAvailableProductsMap().get(productId.toString()));
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void checkAvailability_ShouldReserve_WhenProductsAvailable() {
        when(reservedItemRepository.existsByOrderId(orderId)).thenReturn(false);
        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        ProductRequestDto request = ProductRequestDto.newBuilder()
                .setOrderId(orderId.toString())
                .putOrderItems(productId.toString(), 10L)
                .build();

        AvailabilityProductsDto result = productService.checkAvailability(request);

        assertTrue(result.getIsAvailable());
        assertEquals(10L, result.getAvailableProductsMap().get(productId.toString()));
        verify(reservedItemRepository).saveAll(any());
    }

    @Test
    void checkAvailability_ShouldReturnUnavailable_WhenStockIsLow() {
        product.setQuantity(5L); // Less than requested 10
        when(reservedItemRepository.existsByOrderId(orderId)).thenReturn(false);
        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        ProductRequestDto request = ProductRequestDto.newBuilder()
                .setOrderId(orderId.toString())
                .putOrderItems(productId.toString(), 10L)
                .build();

        AvailabilityProductsDto result = productService.checkAvailability(request);

        assertFalse(result.getIsAvailable());
        assertTrue(result.getUnavailableProductsList().contains(productId.toString()));
    }

    @Test
    void collectItems_ShouldCalculatePricesCorrect() {
        AvailabilityProductsDto availability = AvailabilityProductsDto.newBuilder()
                .putAvailableProducts(productId.toString(), 2L)
                .build();

        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        ProductResponseDto result = productService.collectItems(availability);

        assertEquals(1, result.getItemsCount());
        OrderItemDto item = result.getItems(0);
        assertEquals(10000L, item.getPricePennies());
        assertEquals(1000L, item.getSalePennies());
        assertEquals(18000L, item.getTotalPrice());
    }

    @Test
    void confirmOrder_ShouldDecreaseInventoryAndClearReservations() {
        ReservedItemEntity reservation = ReservedItemEntity.builder()
                .product(product)
                .quantity(10L)
                .build();
        product.getReservedItemEntities().add(reservation);

        when(reservedItemRepository.findAllByOrderId(orderId)).thenReturn(List.of(reservation));

        productService.confirmOrder(ConfirmedOrderId.newBuilder().setId(orderId.toString()).build());

        assertEquals(40L, product.getQuantity());
        assertTrue(product.getReservedItemEntities().isEmpty());
        verify(reservedItemRepository).deleteAll(any());
    }

    @Test
    void cancelConfirmation_ShouldOnlyRemoveReservations() {
        ReservedItemEntity reservation = ReservedItemEntity.builder()
                .product(product)
                .quantity(10L)
                .build();
        product.getReservedItemEntities().add(reservation);

        when(reservedItemRepository.findAllByOrderId(orderId)).thenReturn(List.of(reservation));

        productService.cancelConfirmation(ConfirmedOrderId.newBuilder().setId(orderId.toString()).build());

        assertEquals(50L, product.getQuantity());
        assertTrue(product.getReservedItemEntities().isEmpty());
        verify(reservedItemRepository).deleteAll(any());
    }

    @Test
    void getProductById_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    void convertToPennies_ShouldHandleBigDecimalCorrectly() {
        long result = productService.convertToPennies(new BigDecimal("19.99"));
        assertEquals(1999L, result);
    }
}