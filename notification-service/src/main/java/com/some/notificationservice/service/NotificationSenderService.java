package com.some.notificationservice.service;

import com.some.commonlib.model.event.OrderEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationSenderService {
    void sendNotification(OrderEvent orderEvent);
}
