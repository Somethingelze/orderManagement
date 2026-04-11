package com.some.notificationservice.service.impl;

import com.some.commonlib.annotations.Loggable;
import com.some.notificationservice.model.entity.Order;
import com.some.notificationservice.model.entity.OrderItem;
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

            emailNotificationService.sendOrderConfirmation(order.userEmail(), order.orderId());
            webSocketNotificationService.notifyUser(order.userId(), order.orderId());

        } else if (allUnavailable) {

            emailNotificationService.sendDeclineNotification(order.userEmail(), order.orderId(), unavailableProductName );
            webSocketNotificationService.notifyUserDeclineOrder(order.userId(), order.orderId(), unavailableProductName);

        } else {

            emailNotificationService.sendOrderConfirmation(order.userEmail(), order.orderId(), unavailableProductName);
            webSocketNotificationService.notifyUser(order.userId(), order.orderId(), unavailableProductName);

        }
    }

    @Override
    public List<String> getUnavailableProductName (Order order) {
        return order.orderItems()
                .stream()
                .filter(item -> !item.available())
                .map(OrderItem::name)
                .toList();
    }

}
