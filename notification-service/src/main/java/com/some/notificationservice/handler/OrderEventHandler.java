package com.some.notificationservice.handler;


import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.model.entity.NotificationInboxEntity;
import com.some.notificationservice.repository.NotificationInboxRepository;
import com.some.notificationservice.service.NotificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventHandler {
    private final NotificationSenderService notificationSenderService;
    private final NotificationInboxRepository notificationInboxRepository;

    @KafkaListener(topics = "order.notifications", groupId = "notification-group")
    @Transactional
    public void receiveOrderEvent(OrderEvent event) {
        log.info("Processing event: {} for order: {}", event.eventId(), event.orderId());
        if (notificationInboxRepository.existsById(event.eventId())) {
            log.warn("Duplicate message detected: {}. Skipping.", event.eventId());
            return;
        }

        notificationSenderService.sendNotification(event);
        notificationInboxRepository.save(new NotificationInboxEntity(event.eventId(), LocalDateTime.now()));
    }
}
