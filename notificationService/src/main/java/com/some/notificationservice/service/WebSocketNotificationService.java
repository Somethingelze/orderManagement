package com.some.notificationservice.service;

import java.util.UUID;

public abstract class WebSocketNotificationService {

    public abstract void notifyUser(UUID userId, UUID orderId);
}
