package com.some.notificationservice.service.impl;

import com.some.notificationservice.annotations.Loggable;
import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.entity.OrderItemEntity;
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
    public void sendNotification(OrderEntity orderEntity)   {

        boolean hasUnavailable = !getUnavailableProductName(orderEntity).isEmpty();
        boolean allUnavailable = orderEntity.getOrderItems().stream().noneMatch(OrderItemEntity::isAvailable);
        List<String> unavailableProductName = getUnavailableProductName(orderEntity);

        if(hasUnavailable) {

            emailNotificationService.sendOrderConfirmation(orderEntity.getUserEmail(), orderEntity.getOrderId());
            webSocketNotificationService.notifyUser(orderEntity.getUserId(), orderEntity.getOrderId());

        } else if (allUnavailable) {

            emailNotificationService.sendDeclineNotification(orderEntity.getUserEmail(), orderEntity.getOrderId(), unavailableProductName );
            webSocketNotificationService.notifyUserDeclineOrder(orderEntity.getUserId(), orderEntity.getOrderId(), unavailableProductName);

        } else {

            emailNotificationService.sendOrderConfirmation(orderEntity.getUserEmail(), orderEntity.getOrderId(), unavailableProductName);
            webSocketNotificationService.notifyUser(orderEntity.getUserId(), orderEntity.getOrderId(), unavailableProductName);

        }
    }

    @Override
    public List<String> getUnavailableProductName (OrderEntity orderEntity) {
        return orderEntity.getOrderItems()
                .stream()
                .filter(item -> !item.isAvailable())
                .map(OrderItemEntity::getName)
                .toList();
    }

}
