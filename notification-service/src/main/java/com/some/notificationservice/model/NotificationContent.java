package com.some.notificationservice.model;

public record NotificationContent(
    String subject,
    String body,
    String webSocketMessage
) {}