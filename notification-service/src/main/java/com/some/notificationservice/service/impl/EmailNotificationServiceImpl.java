package com.some.notificationservice.service.impl;

import com.some.notificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl extends EmailNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOrderConfirmation(String to, UUID orderId) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Заказ принят");
            message.setText("Ваш заказ № " + orderId + " успешно обработан и передан в доставку");
            mailSender.send(message);

            log.info("Email sent for order {}", orderId);

        } catch (Exception e) {
            log.error("Failed to send email for order {}", orderId, e);
        }
    }

    @Override
    public void sendOrderConfirmation(String to, UUID orderId, List<String> unavailableProductsNames) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Заказ принят частично");
            message.setText("Заказ № " + orderId + " принят частично. Следующие продукты закончились на складе: "
                    + unavailableProductsNames + ". Пожалуйста, выберите замены.");
            mailSender.send(message);

            log.info("Email sent for partial order {} without products: {} ", orderId, unavailableProductsNames);

        } catch (Exception e) {
            log.error("Failed to send email for order {}", orderId, e);
        }
    }

    @Override
    public void sendDeclineNotification(String to, UUID orderId, List<String> unavailableProductsNames) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Заказ отклонен по причине отсутствия всех товаров на складе.");
            message.setText("Заказ № " + orderId + " отклонен по причине отсутствия следующих товаров на складе: "
                    + unavailableProductsNames + ". Пожалуйста, выберите доступные товары." );

            mailSender.send(message);

            log.info("Email sent for decline order {} without products: {} ", orderId, unavailableProductsNames);

        } catch (Exception e)   {
            log.error("Failed to send email for order {}", orderId, e);
        }
    }
}