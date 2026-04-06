package com.some.notificationservice.service;

import java.util.List;
import java.util.UUID;

public abstract class WebSocketNotificationService {

    public abstract void notifyUser(UUID userId, UUID orderId);

    public abstract void notifyUser(UUID userId, UUID orderId, List<String> unavailableProductsId);

    public abstract void notifyUserDeclineOrder(UUID userId, UUID orderId, List<String> unavailableProductsNames);
}
