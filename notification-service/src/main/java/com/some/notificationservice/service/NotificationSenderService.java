package com.some.notificationservice.service;

import com.some.commonlib.model.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationSenderService {
    void sendNotification(Order order);

    List<String> getUnavailableProductName(Order order);
}
