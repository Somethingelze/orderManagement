package com.some.orderservice.services;

import com.some.orderservice.model.entities.OrderEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OutboxService {
    @Transactional
    void saveAndOutbox(OrderEntity order);
}
