package com.some.notificationservice.service.impl;

import com.some.notificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            message.setText("Ваш заказ #" + orderId + " успешно обработан");
            mailSender.send(message);

            log.info("Email sent for order {}", orderId);

        } catch (Exception e) {
            log.error("Failed to send email for order {}", orderId, e);
        }
    }
}