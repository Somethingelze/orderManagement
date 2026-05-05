package com.some.notificationservice.service.impl;

import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.service.EmailNotificationService;
import com.some.notificationservice.service.NotificationSenderService;
import com.some.notificationservice.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Loggable
public class NotificationSenderServiceImpl implements NotificationSenderService {

    private final EmailNotificationService emailNotificationService;
    private final WebSocketNotificationService webSocketNotificationService;

    @Override
    public void sendNotification(OrderEvent orderEvent)   {
        if (orderEvent.status().equals(Status.UNAVAILABLE)) {
            emailNotificationService.sendDeclineEmail(orderEvent.userEmail(), orderEvent.id(), orderEvent.unavailableProducts());
            webSocketNotificationService.notifyUserDeclineOrder(orderEvent.userId(), orderEvent.id(), orderEvent.unavailableProducts());
        }
        else if (orderEvent.status().equals(Status.PARTIALLY_UNAVAILABLE)) {
            emailNotificationService.sendConfirmationEmail(orderEvent.userEmail(), orderEvent.id(), orderEvent.unavailableProducts());
            webSocketNotificationService.notifyUserConfirmOrder(orderEvent.userId(), orderEvent.id(), orderEvent.unavailableProducts());
        }
        else {
            emailNotificationService.sendConfirmationEmail(orderEvent.userEmail(), orderEvent.id());
            webSocketNotificationService.notifyUserConfirmOrder(orderEvent.userId(), orderEvent.id());
        }
    }
}
