package com.some.notificationservice.factory;

import com.some.notificationservice.model.NotificationContent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class NotificationMessageFactory {

    public NotificationContent buildConfirmation(UUID orderId) {
        String text = "Заказ № " + orderId + " принят в обработку";
        return new NotificationContent("Заказ принят", "Ваш " + text, text);
    }

    public NotificationContent buildPartialConfirmation(UUID orderId, List<String> missing) {
        String details = "Частично принят № " + orderId + ". Нет в наличии: " + missing;
        return new NotificationContent("Заказ принят частично", details, details);
    }

    public NotificationContent buildDecline(UUID orderId, List<String> missing) {
        String details = "Заказ № " + orderId + " отменен. Отсутствуют: " + missing;
        return new NotificationContent("Заказ отклонен", details, details);
    }

    public NotificationContent buildCollected(UUID orderId) {
        String text = "Ваш заказ № " + orderId + " собран!";
        return new NotificationContent("Заказ собран", text, text);
    }

    public NotificationContent buildSuccess(UUID orderId) {
        String text = "Заказ № " + orderId + " готов к отправке!";
        return new NotificationContent("Заказ готов к отправке", text, text);
    }
}