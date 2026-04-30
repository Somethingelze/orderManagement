package com.some.orderservice;

import com.some.commonlib.model.enums.Status;
import com.some.grpc.inventory.*;
import com.some.orderservice.grpc.InventoryGrpcClient;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OrderItemEntity;
import com.some.orderservice.repositories.OrderRepository;
import com.some.orderservice.services.OutboxService;
import com.some.orderservice.services.UserService;
import com.some.orderservice.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private InventoryGrpcClient inventoryClient;
    @Mock private OrderMapper orderMapper;
    @Mock private UserService userService;
    @Mock private OutboxService outboxService;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID userId;
    private OrderRequestDto requestDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        requestDto = new OrderRequestDto(Map.of("product-1", 10L));
        lenient().when(userService.getUserId()).thenReturn(userId);
        lenient().when(userService.getUSerEmail()).thenReturn("test@test.com");
    }

    @Test
    void shouldProcessOrderSuccessfully() {
        AvailabilityProductsDto availability = AvailabilityProductsDto.newBuilder()
                .setIsAvailable(true)
                .putAvailableProducts("product-1", 10L)
                .build();
        when(inventoryClient.checkAvailability(any())).thenReturn(availability);

        ProductResponseDto productResponse = ProductResponseDto.newBuilder()
                .addItems(OrderItemDto.newBuilder().setTotalPrice(10000L).build())
                .build();
        when(inventoryClient.collectOrder(any())).thenReturn(productResponse);

        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setTotalPrice(new BigDecimal("100.00"));

        when(orderMapper.toOrderItemEntity(any())).thenReturn(itemEntity);
        when(orderMapper.toOrderResponseDto(any())).thenReturn(OrderResponseDto.builder().build());

        orderService.processOrder(requestDto);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(outboxService, times(4)).saveAndOutbox(orderCaptor.capture());
        assertEquals(Status.SUCCESS, orderCaptor.getAllValues().get(3).getStatus());
    }

    @Test
    void shouldHandleEmptyProductResponseAsSuccessWithoutItems() {
        AvailabilityProductsDto availability = AvailabilityProductsDto.newBuilder()
                .setIsAvailable(true)
                .putAvailableProducts("product-1", 10L)
                .build();
        when(inventoryClient.checkAvailability(any())).thenReturn(availability);

        when(inventoryClient.collectOrder(any())).thenReturn(ProductResponseDto.newBuilder().build());

        orderService.processOrder(requestDto);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(outboxService, atLeastOnce()).saveAndOutbox(orderCaptor.capture());

        Status finalStatus = orderCaptor.getAllValues().get(orderCaptor.getAllValues().size() - 1).getStatus();
        assertEquals(Status.SUCCESS, finalStatus);
    }

    @Test
    void shouldReturnPageOfOrdersByUserId() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .status(Status.SUCCESS)
                .build();

        Page<OrderEntity> expectedPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAllOrdersByUserId(userId, pageable)).thenReturn(expectedPage);

        Page<OrderEntity> actualPage = orderService.getAllOrdersByUserId(pageable, userId);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(userId, actualPage.getContent().get(0).getUserId());

        verify(orderRepository).findAllOrdersByUserId(userId, pageable);
        verifyNoMoreInteractions(orderRepository);
    }
}