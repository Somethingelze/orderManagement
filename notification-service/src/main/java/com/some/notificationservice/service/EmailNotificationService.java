package com.some.notificationservice.service;

import java.util.List;
import java.util.UUID;

public abstract class EmailNotificationService {

    public abstract void sendOrderConfirmation(String to, UUID orderId);

    public abstract void sendOrderConfirmation(String to, UUID orderId, List<String> unavailableProductsId);

    public abstract void sendDeclineNotification(String to, UUID orderId, List<String> unavailableProductsNames);
}
