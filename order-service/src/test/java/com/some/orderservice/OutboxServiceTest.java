package com.some.orderservice;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OutboxEventEntity;
import com.some.orderservice.repositories.OrderRepository;
import com.some.orderservice.repositories.OutboxRepository;
import com.some.orderservice.services.impl.OutboxServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    private OrderEntity order;
    private OrderEvent orderEvent;

    @BeforeEach
    void setUp() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        order = OrderEntity.builder()
                .id(orderId)
                .userId(userId)
                .userEmail("test@test.com")
                .status(Status.CREATED)
                .totalPrice(new BigDecimal("100.00"))
                .orderItems(Collections.emptyList())
                .build();

        orderEvent = OrderEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(orderId)
                .status(Status.CREATED)
                .userId(userId)
                .userEmail("test@test.com")
                .totalPrice(new BigDecimal("100.00"))
                .orderItems(Collections.emptyList())
                .build();
    }

    @Test
    void shouldSaveOrderAndOutboxEventSuccessfully() {
        when(orderMapper.toOrderEvent(order)).thenReturn(orderEvent);

        outboxService.saveAndOutbox(order);

        verify(orderRepository).save(order);
        verify(orderMapper).toOrderEvent(order);

        ArgumentCaptor<OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(outboxRepository).save(outboxCaptor.capture());

        OutboxEventEntity capturedEvent = outboxCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("ORDER", capturedEvent.getAggregateType());
        assertEquals(order.getId(), capturedEvent.getAggregateId());
        assertEquals(order.getStatus(), capturedEvent.getStatus());
        assertEquals(orderEvent, capturedEvent.getPayload());
    }

    @Test
    void shouldUpdateOutboxWhenOrderStatusChanges() {
        order.setStatus(Status.SUCCESS);
        OrderEvent successEvent = OrderEvent.builder()
                .orderId(order.getId())
                .status(Status.SUCCESS)
                .build();

        when(orderMapper.toOrderEvent(order)).thenReturn(successEvent);

        outboxService.saveAndOutbox(order);

        ArgumentCaptor<OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(outboxRepository).save(outboxCaptor.capture());

        assertEquals(Status.SUCCESS, outboxCaptor.getValue().getStatus());
        assertEquals(Status.SUCCESS, ((OrderEvent) outboxCaptor.getValue().getPayload()).status());
    }
}