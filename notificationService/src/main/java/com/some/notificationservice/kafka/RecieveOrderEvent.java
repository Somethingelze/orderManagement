package com.some.notificationservice.kafka;


import com.some.notificationservice.model.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
@Slf4j
public class RecieveOrderEvent {

    private final KafkaListener kafkaListener;

    @KafkaListener(topics = "order-event")
    public OrderEvent recieveOrderEvent (OrderEvent orderEvent)    {
        log.info("Recieved order event {}", orderEvent);

        //TODO отправка на почту или WebSocket

        return orderEvent;
    }
}
