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
        if (Status.REJECTED.equals(orderEvent.status())) {
            emailNotificationService.sendDeclineEmail(orderEvent.userEmail(), orderEvent.orderId(), orderEvent.unavailableProducts());
            webSocketNotificationService.notifyUserDeclineOrder(orderEvent.userId(), orderEvent.orderId(), orderEvent.unavailableProducts());
        } else if (Status.PARTIAL_RESERVED.equals(orderEvent.status())) {
            emailNotificationService.sendConfirmationEmail(orderEvent.userEmail(), orderEvent.orderId(), orderEvent.unavailableProducts());
            webSocketNotificationService.notifyUserConfirmOrder(orderEvent.userId(), orderEvent.orderId(), orderEvent.unavailableProducts());
        } else if (Status.CREATED.equals(orderEvent.status()))  {
            emailNotificationService.sendConfirmationEmail(orderEvent.userEmail(), orderEvent.orderId());
            webSocketNotificationService.notifyUserConfirmOrder(orderEvent.userId(), orderEvent.orderId());
        } else if (Status.SUCCESS.equals(orderEvent.status())) {
            
        } else if (Status.COLLECTED.equals(orderEvent.status())) {
            
        } else if (Status.RESERVED.equals(orderEvent.status())) {
            
        }
    }
}
