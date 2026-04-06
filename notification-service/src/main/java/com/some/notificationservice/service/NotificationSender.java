package com.some.notificationservice.service;

import com.some.notificationservice.model.entity.OrderEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class NotificationSender {
    public abstract void sendNotification(OrderEntity orderEntity);

    public abstract List<String> getUnavailableProductName(OrderEntity orderEntity);
}
