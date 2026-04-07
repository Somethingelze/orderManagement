package com.some.notificationservice.handler;


import com.some.notificationservice.annotations.Loggable;
import com.some.notificationservice.mapper.OrderMapper;
import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.entity.OrderItemEntity;
import com.some.notificationservice.model.event.OrderEvent;
import com.some.notificationservice.repository.OrderRepository;
import com.some.notificationservice.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Loggable
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final NotificationSender notificationSender;


    @KafkaListener(topics = "order-event")
    @Transactional
    public void receiveOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event {}", orderEvent.orderId());

        if (orderRepository.existsByOrderId(orderEvent.orderId())) {
            log.warn("Order {} already processed. Skipping.", orderEvent.orderId());
            return;
        }

        OrderEntity orderEntity = orderMapper.orderEventToOrderEntity(orderEvent);
        orderRepository.save(orderEntity);
        log.info("Order {} has been saved", orderEvent.orderId());

        notificationSender.sendNotification(orderEntity);
        log.info("Notification for order {} has been sending", orderEntity.getOrderId());
    }


}
