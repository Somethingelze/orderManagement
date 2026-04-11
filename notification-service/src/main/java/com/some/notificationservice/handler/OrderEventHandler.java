package com.some.notificationservice.handler;


import com.some.commonlib.annotations.Loggable;
import com.some.notificationservice.mapper.OrderMapper;
import com.some.notificationservice.model.entity.Order;
import com.some.notificationservice.model.event.OrderEvent;
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

    private final OrderMapper orderMapper;
    private final NotificationSender notificationSender;


    @KafkaListener(topics = "order-event")
    @Transactional
    public void receiveOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event {}", orderEvent.orderId());

        Order order = orderMapper.orderEventToOrderEntity(orderEvent);
        log.info("Order {} has been saved", orderEvent.orderId());

        notificationSender.sendNotification(order);
        log.info("Notification for order {} has been sending", order.orderId());
    }


}
