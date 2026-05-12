package com.some.notificationservice.service;

import java.util.UUID;

public interface NotificationService {

    void sendEmail(String to, String subject, String body);

    void sendNotification(UUID userId, String message);
}
