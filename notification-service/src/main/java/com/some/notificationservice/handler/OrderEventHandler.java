package com.some.notificationservice.handler;


import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.model.entity.NotificationInboxEntity;
import com.some.notificationservice.repository.NotificationInboxRepository;
import com.some.notificationservice.service.NotificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final NotificationSenderService notificationSenderService;
    private final NotificationInboxRepository notificationInboxRepository;

    @KafkaListener(
            topics = "#{@orderConsumerProperties.topic()}",
            groupId = "#{@orderConsumerProperties.groupId()}"
    )
    public void receiveOrderEvent(OrderEvent event) {
        log.info("Processing event: {} for order: {}", event.eventId(), event.orderId());
        if (notificationInboxRepository.existsById(event.eventId())) {
            log.warn("Duplicate message detected: {}. Skipping.", event.eventId());
            return;
        }

        notificationInboxRepository.save(new NotificationInboxEntity(event.eventId(), LocalDateTime.now()));
        notificationSenderService.sendNotification(event);
    }
}
