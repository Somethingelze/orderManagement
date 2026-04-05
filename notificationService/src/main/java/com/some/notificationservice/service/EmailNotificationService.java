package com.some.notificationservice.service;

import java.util.UUID;

public abstract class EmailNotificationService {

    public abstract void sendOrderConfirmation(String to, UUID orderId);
}
