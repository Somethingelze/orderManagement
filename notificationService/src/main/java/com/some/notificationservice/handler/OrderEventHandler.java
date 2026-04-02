package com.some.notificationservice.handler;


import com.some.notificationservice.mapper.OrderMapper;
import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.event.OrderEvent;
import com.some.notificationservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    @KafkaListener(topics = "order-event")
    public OrderEntity receiveOrderEvent(OrderEvent orderEvent)    {
        log.info("Received order event {}", orderEvent);
        OrderEntity orderEntity = orderMapper.orderEventToOrderEntity(orderEvent);
        return orderRepository.save(orderEntity);


        //TODO отправка на почту или WebSocket
    }
}
