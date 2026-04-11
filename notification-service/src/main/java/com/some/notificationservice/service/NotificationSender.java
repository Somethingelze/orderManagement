package com.some.notificationservice.service;

import com.some.notificationservice.model.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class NotificationSender {
    public abstract void sendNotification(Order order);

    public abstract List<String> getUnavailableProductName(Order order);
}
