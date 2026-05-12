package com.some.notificationservice.service.impl;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.model.NotificationContent;
import com.some.notificationservice.factory.NotificationMessageFactory;
import com.some.notificationservice.service.NotificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSenderServiceImpl implements NotificationSenderService {

    private final NotificationServiceImpl notificationService;
    private final NotificationMessageFactory messageFactory;


    @Override
    public void sendNotification(OrderEvent event) {
        NotificationContent content = switch (event.status()) {
            case CREATED, RESERVED -> messageFactory.buildConfirmation(event.orderId());
            case PARTIAL_RESERVED ->
                    messageFactory.buildPartialConfirmation(event.orderId(), event.unavailableProducts());
            case REJECTED -> messageFactory.buildDecline(event.orderId(), event.unavailableProducts());
            case COLLECTED -> messageFactory.buildCollected(event.orderId());
            case SUCCESS -> messageFactory.buildSuccess(event.orderId());
            default -> {
                log.info("No notification needed for status: {}", event.status());
                yield null;
            }
        };

        if (content != null) {
            if (event.status() != Status.COLLECTED && event.status() != Status.SUCCESS) {
                notificationService.sendEmail(event.userEmail(), content.subject(), content.body());
            }
            notificationService.sendNotification(event.userId(), content.webSocketMessage());
        }
    }
}

