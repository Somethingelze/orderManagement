package com.some.notificationservice.handler;


import com.some.notificationservice.mapper.OrderMapper;
import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.event.OrderEvent;
import com.some.notificationservice.repository.OrderRepository;
import com.some.notificationservice.service.EmailNotificationService;
import com.some.notificationservice.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final EmailNotificationService emailNotificationService;
    private final WebSocketNotificationService webSocketNotificationService;


    @KafkaListener(topics = "order-event")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receiveOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event {}", orderEvent.orderId());

        if (orderRepository.existsByOrderId(orderEvent.orderId())) {
            log.warn("Order {} already processed. Skipping...", orderEvent.orderId());
            return;
        }

        OrderEntity orderEntity = orderMapper.orderEventToOrderEntity(orderEvent);
        orderRepository.save(orderEntity);
        log.info("Order {} has been saved", orderEvent.orderId());

        sendNotifications(orderEvent.orderId(), orderEvent.userId(), orderEvent.email());
        log.info("Notifications for order: {} have been sent", orderEvent.orderId());
    }

    public void sendNotifications(UUID orderId, UUID userId, String to) {
        emailNotificationService.sendOrderConfirmation(to, orderId);
        webSocketNotificationService.notifyUser(userId, orderId);
    }
}
