package com.some.notificationservice.service.impl;

import com.some.notificationservice.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationServiceImpl extends WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyUser(UUID userId, UUID orderId) {

        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/orders", 
            "Заказ номер: " + orderId + " принят в обработку"
        );
    }
}