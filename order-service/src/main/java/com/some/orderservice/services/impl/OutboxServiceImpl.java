package com.some.orderservice.services.impl;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OutboxEventEntity;
import com.some.orderservice.repositories.OrderRepository;
import com.some.orderservice.repositories.OutboxRepository;
import com.some.orderservice.services.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {
    private final OutboxRepository outboxRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void saveAndOutbox(OrderEntity order) {
        orderRepository.save(order);

        OrderEvent event = orderMapper.toOrderEvent(order);
            OutboxEventEntity outbox = OutboxEventEntity.builder()
                    .aggregateType("ORDER")
                    .aggregateId(order.getId())
                    .status(order.getStatus())
                    .payload(event)
                    .build();
            outboxRepository.save(outbox);
    }
}
