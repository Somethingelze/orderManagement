package com.some.notificationservice.handler;


import com.some.notificationservice.mapper.OrderMapper;
import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.event.OrderEvent;
import com.some.notificationservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    @KafkaListener(topics = "order-event")
    public void receiveOrderEvent(OrderEvent orderEvent)    {
        log.info("Received order event {}", orderEvent.orderId());

        if (orderRepository.existsByOrderId(orderEvent.orderId())) {
            log.warn("Order {} already processed. Skipping...", orderEvent.orderId());
            return;
        }

        OrderEntity orderEntity = orderMapper.orderEventToOrderEntity(orderEvent);
        orderRepository.save(orderEntity);
        log.info("Order {} has been saved", orderEvent.orderId());

        //TODO отправка на почту или WebSocket
    }
}
