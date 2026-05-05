package com.some.notificationservice.service.impl;

import com.some.notificationservice.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationServiceImpl extends WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyUserConfirmOrder(UUID userId, UUID orderId) {

        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/orders", 
            "Заказ номер: " + orderId + " принят в обработку"
        );
        log.info("WebSocket sent for order {}", orderId);

    }

    @Override
    public void notifyUserConfirmOrder(UUID userId, UUID orderId, List<String> unavailableProducts)  {

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/orders",
                "Заказ № " + orderId + " принят частично. Следующие продукты закончились на складе: " + unavailableProducts
        );
        log.info("WebSocket sent for partial order {} without products: {} ", orderId, unavailableProducts);
    }

    @Override
    public void notifyUserDeclineOrder(UUID userId, UUID orderId, List<String> unavailableProducts)    {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/orders",
                "Заказ № " + orderId + " отменен. Следующие продукты закончились на складе: " + unavailableProducts
                );
        log.info("WebSocket sent for decline order {} without products: {} ", orderId, unavailableProducts);

    }
}