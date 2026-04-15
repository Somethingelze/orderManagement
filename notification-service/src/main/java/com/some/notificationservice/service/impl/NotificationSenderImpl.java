package com.some.notificationservice.service.impl;

import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.entity.Order;
import com.some.commonlib.model.entity.OrderItem;
import com.some.notificationservice.service.EmailNotificationService;
import com.some.notificationservice.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Loggable
public class NotificationSenderImpl extends NotificationSender {

    private final EmailNotificationService emailNotificationService;
    private final WebSocketNotificationServiceImpl webSocketNotificationService;

    @Override
    public void sendNotification(Order order)   {

        boolean hasUnavailable = !getUnavailableProductName(order).isEmpty();
        boolean allUnavailable = order.orderItems().stream().noneMatch(OrderItem::available);
        List<String> unavailableProductName = getUnavailableProductName(order);

        if(hasUnavailable) {

            emailNotificationService.sendOrderConfirmation(order.userEmail(), order.id());
            webSocketNotificationService.notifyUser(order.userId(), order.id());

        } else if (allUnavailable) {

            emailNotificationService.sendDeclineNotification(order.userEmail(), order.id(), unavailableProductName );
            webSocketNotificationService.notifyUserDeclineOrder(order.userId(), order.id(), unavailableProductName);

        } else {

            emailNotificationService.sendOrderConfirmation(order.userEmail(), order.id(), unavailableProductName);
            webSocketNotificationService.notifyUser(order.userId(), order.id(), unavailableProductName);

        }
    }

    @Override
    public List<String> getUnavailableProductName (Order order) {
        return order.orderItems()
                .stream()
                .filter(item -> !item.available())
                .map(OrderItem::productName)
                .toList();
    }

}
