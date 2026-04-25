package com.some.notificationservice.service;

import java.util.List;
import java.util.UUID;

public abstract class EmailNotificationService {

    public abstract void sendConfirmationEmail(String to, UUID orderId);

    public abstract void sendConfirmationEmail(String to, UUID orderId, List<String> unavailableProducts);

    public abstract void sendDeclineEmail(String to, UUID orderId, List<String> unavailableProducts);
}
