package com.some.notificationservice.handler;


import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.service.NotificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Loggable
public class OrderEventHandler {

    private final NotificationSenderService notificationSenderService;


    @KafkaListener(topics = "order-event")
    public void receiveOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event {}", orderEvent.id());

        notificationSenderService.sendNotification(orderEvent);
        log.info("Notification for order {} has been sending ", orderEvent.id());
    }


}
